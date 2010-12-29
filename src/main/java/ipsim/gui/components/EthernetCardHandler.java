package ipsim.gui.components;

import static ipsim.util.Collections.mapWith;
import fpeas.function.Function;
import fpeas.function.FunctionUtility;
import static fpeas.function.math.Doubles.abs;
import static fpeas.function.math.Doubles.lessThan;
import static fpeas.function.math.Doubles.minus;
import fpeas.maybe.Maybe;
import static fpeas.maybe.MaybeUtility.bind2;
import static fpeas.maybe.MaybeUtility.constIfNothing;
import fpeas.predicate.Predicate;
import ipsim.Caster;
import ipsim.NetworkContext;
import static ipsim.NetworkContext.errors;
import ipsim.awt.Point;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.gui.ObjectRenderer;
import ipsim.gui.PacketSourceAndPoints;
import static ipsim.gui.PositionUtility.getParent;
import static ipsim.gui.PositionUtility.getPosition;
import static ipsim.gui.PositionUtility.hasParent;
import static ipsim.gui.PositionUtility.numPositions;
import static ipsim.gui.PositionUtility.removePositions;
import static ipsim.gui.PositionUtility.setParent;
import static ipsim.gui.PositionUtility.setPosition;
import static ipsim.gui.components.ContextMenuUtility.item;
import ipsim.gui.event.MouseTracker;
import ipsim.image.ImageLoader;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.textmetrics.HorizontalAlignment;
import ipsim.textmetrics.TextMetrics;
import ipsim.textmetrics.VerticalAlignment;
import ipsim.util.Collections;
import static ipsim.util.Collections.any;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Map;

public final class EthernetCardHandler
{
	public static final ImageIcon icon=ImageLoader.loadImage(EthernetCardHandler.class.getResource("/images/card.png"));

	public static Card create(final Network network, final int x, final int y)
	{
		final Card card=new Card();
		final Map<Integer, Point> map=Collections.hashMap();
		map.put(0,new Point(x,y));
		network.topLevelComponents.add(new PacketSourceAndPoints(card,map));

		return card;
	}

	public static void render(final NetworkContext context, final Card card, final Graphics2D graphics)
	{
		if (numPositions(card)==0)
			throw new RuntimeException("Cannot render this card");

		final Point position=getPosition(context.network,card,0);

		final Image cardImage=icon.getImage();

		final int imageWidth=cardImage.getWidth(null)/2;

		final int imageHeight=cardImage.getHeight(null)/2;

		graphics.drawImage(cardImage,(int)position.x-imageWidth,(int)position.y-imageHeight, context.networkView);

		final MouseTracker mouseTracker=context.mouseTracker;

		final Maybe<Double> mouseX=bind2(mouseTracker.getX(),divideBy(context.zoomLevel));
		final Maybe<Double> mouseY=bind2(mouseTracker.getY(),divideBy(context.zoomLevel));

		final Function<Double,Double> identity=FunctionUtility.identity();

		boolean mouseIsNear=constIfNothing(mouseX,false,lessThan(abs(minus(identity, position.x)),40));
		mouseIsNear&=constIfNothing(mouseY,false,lessThan(abs(minus(identity, position.y)),40));

		// look at parent to find out what index card has
		if (hasParent(context.network,card,0)&& card.hasDeviceDrivers()&&mouseIsNear)
		{
			final int index=card.withDrivers.ethNumber;

			TextMetrics.drawString(graphics,"Card "+index,(int)position.x,(int)position.y+imageHeight/2+5,HorizontalAlignment.CENTRE,VerticalAlignment.TOP,true);
		}
	}

	public static void componentMoved(final Network network, final Card card,final int pointIndex)
	{
		// An EthernetCard can be attached to a Computer.
		// Is there an Computer 'near' the moved point?

		final Iterable<PacketSource> childNodes=NetworkUtility.getDepthFirstIterable(network);

		for (final PacketSource component2: childNodes)
		{
			if (!PacketSourceUtility.isComputer(component2))
				continue;

			final int size=numPositions(component2);

			for (int a=0;a<size;a++)
				if (ObjectRenderer.isNear(network, card,pointIndex,component2,a))
				{
					try
					{
						setParent(network,card,0,component2,0);
					}
					catch (final IllegalStateException exception)
					{
					}
					return;
				}
		}
	}

	public static JPopupMenu createContextMenu(final NetworkContext context,final Card card)
	{
		final JPopupMenu menu=new JPopupMenu();
		menu.add(item("Install/Uninstall Device Drivers",'I', new Runnable()
		{
			@Override
            public void run()
			{
				if (card.hasDeviceDrivers())
					card.uninstallDeviceDrivers();
				else
					card.installDeviceDrivers(context.network);

				context.networkView.repaint();
			}
		}));
		menu.add(item("Disconnect cable from card",'C', new Runnable()
		{
			@Override
            public void run()
			{
				final @Nullable Cable cable=card.getCable();

				if (cable==null)
				{
					errors("There is no cable to disconnect");
					return;
				}

				for (final Integer a: new int[]{0,1})
				{
					final PacketSource parent=getParent(context.network, cable, a);
					if (parent!=null && Caster.equalT(parent,card))
						setPosition(context.network,cable, mapWith(a, getPosition(context.network,card,0)));
				}
			}
		}));

		menu.add(item("Delete",'D', new Runnable()
		{
			@Override
            public void run()
			{
				if (card.hasDeviceDrivers())
					NetworkContext.errors("You must remove the drivers first");
				else
				{
					final Network network=context.network;
					network.log=Collections.add(network.log,"Deleted "+PacketSourceUtility.asString(network, card)+'.');

					removePositions(network,card,context.networkView);

					final Predicate<PacketSource> equalT=Caster.<PacketSource>equalT(card);

					if (any(NetworkUtility.getDepthFirstIterable(network), equalT))
						removePositions(network,card,context.networkView);

					context.networkView.repaint();
				}
			}
		}));

		return menu;
	}

	public static Function<Integer,Double> divideBy(final double divisor)
	{
		return new Function<Integer,Double>()
		{
			@Override
            @NotNull
			public Double run(@NotNull final Integer divisible)
			{
				return divisible/divisor;
			}
		};
	}
}