package ipsim.gui;

import com.rickyclarkson.testsuite.UnitTest;
import fpeas.predicate.Predicate;
import fpeas.predicate.PredicateUtility;
import fpeas.sideeffect.SideEffect;
import ipsim.Caster;
import ipsim.NetworkContext;
import ipsim.awt.Point;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.card.Card;
import ipsim.property.Property;
import ipsim.property.PropertyListener;
import ipsim.property.PropertyUtility;
import ipsim.util.Collections;
import static ipsim.util.Collections.arrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Collection;

public class NetworkView extends JPanel
{
	public final Property<Boolean> ignorePaints=PropertyUtility.newProperty(false);

	public final Collection<PacketSource> visibleComponents=arrayList();

	public final NetworkContext context;

	public NetworkView(final NetworkContext context)
	{
		this.context=context;

		ignorePaints.addPropertyListener(new PropertyListener<Boolean>()
		{
			@Override
            public void propertyChanged(final Property<Boolean> property, final Boolean oldValue, final Boolean newValue)
			{
				repaint();
			}
		});

		paintComponent=new SideEffect<Graphics>()
		{
			@Override
            public void run(final Graphics originalGraphics)
			{
				if (ignorePaints.get())
					return;

				final Graphics graphics=originalGraphics.create();

				final Graphics2D g2d=Caster.asGraphics2D(graphics);

				final AffineTransform transform=g2d.getTransform();
				final double zoomLevel=context.zoomLevel;
				transform.scale(zoomLevel, zoomLevel);
				g2d.setTransform(transform);

				/*
				 * EthernetCables get drawn first, and hence do not appear above the cards and hubs that they are connected to.
				*/

				final Predicate<PacketSource> cables=new Predicate<PacketSource>()
				{
					@Override
                    public boolean invoke(final PacketSource component1)
					{
						return PacketSourceUtility.isCable(component1);
					}
				};

				for (final PacketSource next : Collections.only(visibleComponents, cables))
					ObjectRenderer.render(context, next, g2d);

				final Predicate<PacketSource> cards=new Predicate<PacketSource>()
				{
					@Override
                    public boolean invoke(final PacketSource component1)
					{
						return PacketSourceUtility.isCard(component1);
					}
				};

				final Predicate<PacketSource> cablesOrCards=new Predicate<PacketSource>()
				{
					@Override
                    public boolean invoke(final PacketSource component1)
					{
						return PacketSourceUtility.isCard(component1) || PacketSourceUtility.isCable(component1);
					}
				};

				for (final PacketSource next : Collections.only(visibleComponents, PredicateUtility.not(cablesOrCards)))
					ObjectRenderer.render(context, next, g2d);

				for (final PacketSource next : Collections.only(visibleComponents, cards))
					ObjectRenderer.render(context, next, g2d);

				// render cables, then others, then cards
			}
		};
	}

	/**
	 * Iterates through the network, painting each item to the display.
	 */
	@Override
	public void paintComponent(final Graphics originalGraphics)
	{
		super.paintComponent(originalGraphics);

		paintComponent.run(originalGraphics);
	}

	public final SideEffect<Graphics> paintComponent;

	public Card newCard(final Point point)
	{
		final Card card=context.network.cardFactory.run(point);
		visibleComponents.add(card);
		return card;
	}

	public static final UnitTest testAddingComponentToANetworkAddsItToView=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final NetworkContext context=new NetworkContext(null);
			final Card card=context.networkView.newCard(new Point(10,10));
			return context.networkView.visibleComponents.contains(card);
		}

		public String toString()
		{
			return "testAddingComponentToANetworkAddsItToView";
		}
	};
}