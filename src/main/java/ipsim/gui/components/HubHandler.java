package ipsim.gui.components;

import ipsim.NetworkContext;
import static ipsim.NetworkContext.confirm;
import ipsim.awt.Point;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.gui.PacketSourceAndPoints;
import static ipsim.gui.PositionUtility.getPosition;
import static ipsim.gui.PositionUtility.numPositions;
import static ipsim.gui.PositionUtility.removePositions;
import static ipsim.gui.components.ContextMenuUtility.item;
import ipsim.gui.event.CommandUtility;
import static ipsim.gui.event.CommandUtility.enableHubPower;
import ipsim.image.ImageLoader;
import ipsim.network.Network;
import ipsim.network.connectivity.hub.Hub;
import static ipsim.util.Collections.hashMap;
import ipsim.util.Collections;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public final class HubHandler
{
	public static final ImageIcon icon=ImageLoader.loadImage(HubHandler.class.getResource("/images/hub.png"));

	public static Hub create(final Network network, final int x, final int y)
	{
		final Hub hub=new Hub(network);

		final Map<Integer,Point> points=hashMap();
		points.put(0,new Point((double)x, (double)y));

		network.topLevelComponents.add(new PacketSourceAndPoints(hub,points));

		return hub;
	}

	public static void render(final NetworkContext context, final Hub hub,final Graphics2D graphics)
	{
		if (numPositions(hub)==0)
			return;

		final Point position=getPosition(context.network,hub,0);

		final Image hubImage=icon.getImage();

		final int imageWidth=hubImage.getWidth(null);
		final int imageHeight=hubImage.getHeight(null);

		graphics.drawImage(hubImage,(int)position.x-imageWidth/2,(int)position.y-imageHeight/2, context.networkView);

		final Color originalColor=graphics.getColor();

		graphics.setColor(Color.green.brighter());

		if (hub.isPowerOn())
			graphics.fillOval((int)position.x-imageWidth/3-4,(int)position.y-imageHeight/3+4,8,8);

		graphics.setColor(originalColor);
	}

	public static JPopupMenu createContextMenu(final NetworkContext context, final Hub hub)
	{
		final JPopupMenu menu=new JPopupMenu();

		menu.add(item("Delete",'D', new Runnable()
		{
			@Override
            public void run()
			{
				if (confirm("Really delete this hub?"))
				{
					final Network network=context.network;
					network.log=Collections.add(network.log,"Deleted "+PacketSourceUtility.asString(network, hub)+'.');
					removePositions(network,hub,context.networkView);
					network.modified=true;
					context.networkView.repaint();
				}
			}
		}));

		final JRadioButtonMenuItem powerItem=new JRadioButtonMenuItem("Toggle Power");
		powerItem.setMnemonic('T');
		powerItem.setSelected(hub.isPowerOn());

		powerItem.addActionListener(new ActionListener()
		{
			@Override
            public void actionPerformed(final ActionEvent event)
			{
				final Network network=context.network;
				if (powerItem.isSelected())
				{
					hub.setPower(true);
					network.modified=true;
					network.log=Collections.add(network.log,enableHubPower(hub, network));
				}
				else
				{
					hub.setPower(false);
					network.modified=true;
					network.log=Collections.add(network.log,CommandUtility.disableHubPower(hub, network));
				}
			}
		});

		menu.add(powerItem);

		return menu;
	}
}