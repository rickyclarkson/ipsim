package ipsim.gui.components;

import ipsim.Caster;
import ipsim.Global;
import ipsim.NetworkContext;
import static ipsim.NetworkContext.confirm;
import ipsim.awt.Point;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.gui.JOptionPaneUtility;
import static ipsim.gui.ObjectRenderer.isNear;
import static ipsim.gui.PositionUtility.getPosition;
import static ipsim.gui.PositionUtility.numPositions;
import static ipsim.gui.PositionUtility.removePositions;
import static ipsim.gui.PositionUtility.setParent;
import ipsim.gui.UserMessages;
import static ipsim.gui.components.ContextMenuUtility.item;
import ipsim.image.ImageLoader;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.cable.CableType;
import ipsim.util.Collections;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Vector;

public class EthernetCableHandler
{
	public static final ImageIcon icon=ImageLoader.loadImage(EthernetCableHandler.class.getResource("/images/cable.png"));

	public static void render(final Network network, final Cable cable, final Graphics2D graphics2d)
	{
		final Stroke initialStroke=graphics2d.getStroke();
		final Color initialColor=graphics2d.getColor();

		graphics2d.setColor(Color.gray.brighter());

		graphics2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		final Point position1=getPosition(network,cable,0);
		final Point position2=getPosition(network,cable,1);

		graphics2d.drawLine((int)position1.x, (int)position1.y, (int)position2.x, (int)position2.y);

		graphics2d.setStroke(initialStroke);
		graphics2d.setColor(initialColor);
	}

	public static void componentMoved(final Network network, final Cable cable, final int... points)
	{
		for (final int pointIndex : points)
			for (final PacketSource next : NetworkUtility.getDepthFirstIterable(network))
			{
				if (!(PacketSourceUtility.isCard(next) || PacketSourceUtility.isHub.invoke(next)))
					continue;

				// iterate through all points of component2
				final int size=numPositions(next);

				for (int a=0;a<size;a++)
					if (isNear(network, cable, pointIndex, next, a))
						setParent(network,cable, pointIndex, next, a);
			}
	}

	public static JPopupMenu createContextMenu(final Cable cable)
	{
		final JPopupMenu menu=new JPopupMenu();
		menu.add(item("Delete", 'D', new Runnable()
		{
			@Override
            public void run()
			{
				if (confirm("Really delete this Ethernet cable?"))
				{
					final NetworkContext networkContext=Global.getNetworkContext();
					final Network network=networkContext.network;
					network.log=Collections.add(network.log,"Deleted "+PacketSourceUtility.asString(network, cable)+'.');

					removePositions(networkContext.network,cable, networkContext.networkView);
					networkContext.networkView.repaint();
					network.modified=true;
				}
			}
		}));
		menu.add(item("Test Cable", 'T', new Runnable()
		{
			@Override
            public void run()
			{
				final String result=cable.getCableType().asString()+" cable.";

				final Network network=Global.getNetworkContext().network;
				network.log=Collections.add(network.log,"Tested a cable, result: "+result);

				UserMessages.message(result);
			}
		}));
		menu.add(item("Change Cable Type", 'C', new Runnable()
		{
			@Override
            public void run()
			{
				final JList list=new JList();
				list.setListData(new Vector<CableType>(Arrays.asList(CableType.STRAIGHT_THROUGH, CableType.CROSSOVER)));

				if (cable.getCableType().equals(CableType.BROKEN))
					list.clearSelection();
				else
					list.setSelectedValue(cable.getCableType(), true);

				final int result=JOptionPaneUtility.showConfirmDialog(Global.global.get().frame, list, "Cable Type", JOptionPane.OK_CANCEL_OPTION);

				final CableType cableType=Caster.asCableType(list.getSelectedValue());
				if (result==JOptionPane.OK_OPTION && cableType!=null)
					cable.setCableType(cableType);
			}
		}));

		return menu;
	}

}