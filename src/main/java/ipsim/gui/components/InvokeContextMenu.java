package ipsim.gui.components;

import ipsim.NetworkContext;
import ipsim.awt.Point;
import ipsim.gui.ObjectRenderer;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;

import javax.swing.*;
import java.awt.*;

public class InvokeContextMenu
{
	public static void invokeContextMenu(final JPopupMenu menu,final NetworkContext context, final PacketSource component)
	{
		component.accept(new PacketSourceVisitor2()
		{
			@Override
            public void visit(final Card card)
			{
				final Point centre=ObjectRenderer.getCentre(context.network, card);

				final Component networkView=context.networkView;

				menu.show(networkView,(int)(centre.x*context.zoomLevel),(int)(centre.y*context.zoomLevel));
			}

			@Override
            public void visit(final Computer computer)
			{
				final Point centre=ObjectRenderer.getCentre(context.network, computer);

				final Component networkView=context.networkView;
				menu.show(networkView, (int)(centre.x*context.zoomLevel), (int)(centre.y*context.zoomLevel));
			}

			@Override
            public void visit(final Cable cable)
			{
				final Point centre=ObjectRenderer.getCentre(context.network,cable);

				final Component networkView=context.networkView;

				menu.show(networkView,(int)(centre.x*context.zoomLevel),(int)(centre.y*context.zoomLevel));
			}

			@Override
            public void visit(final Hub hub)
			{
				final Point centre=ObjectRenderer.getCentre(context.network, hub);

				final Component networkView=context.networkView;

				menu.show(networkView,(int)(centre.x*context.zoomLevel),(int)(centre.y*context.zoomLevel));
			}
		});
	}
}