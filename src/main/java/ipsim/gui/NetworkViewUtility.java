package ipsim.gui;

import ipsim.Caster;
import ipsim.NetworkContext;
import ipsim.awt.Point;
import static ipsim.gui.PositionUtility.centreOf;
import static ipsim.gui.PositionUtility.getPosition;
import static ipsim.gui.PositionUtility.hasParent;
import static ipsim.gui.PositionUtility.numPositions;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.PacketSource;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;

/**
 * Class which is responsible for the main display - i.e., the network, and the first stage in user interaction with the network.
 */
public final class NetworkViewUtility
{

	/**
	 * Searches the network to find the point that is closest to (x,y).
	 */
	public static PacketSource getPointAt(final Network network, final int x, final int y)
	{
		final Iterable<PacketSource> iterable=NetworkUtility.getDepthFirstIterable(network);

		PacketSource answer=null;

		double distance=Double.MAX_VALUE;

		for (final PacketSource next : iterable)
		{
			final Point middle=centreOf(network, next);

			final double xDistance=middle.x-x;
			final double yDistance=middle.y-y;

			final double newDistance=Math.sqrt(xDistance*xDistance+yDistance*yDistance);

			if (newDistance<distance)
			{
				distance=newDistance;
				answer=next;
			}
		}

		return answer;
	}

	public static NetworkView newNetworkView(final NetworkContext context)
	{
		final NetworkView component=new NetworkView(context);

		final MouseInputListener listener=NetworkViewMouseListenerUtility.createNetworkViewMouseListener(context);
		component.addMouseListener(listener);
		component.addMouseMotionListener(listener);
		component.setAutoscrolls(true);
		component.setBackground(Color.white);

		return component;
	}

	public static final class PointRecordDead
	{
		public PacketSource object;

		public int index;

		/**
		 * A utility constructor to provide easy initialisation of a PointRecord.
		 */
		PointRecordDead(final PacketSource object, final int index)
		{
			this.object=object;
			this.index=index;
		}
	}

	public static PointRecordDead getTopLevelPointAt(final Network network, final int x, final int y)
	{
		final PointRecordDead answer=new PointRecordDead(null, -1);

		final Iterable<PacketSource> children=NetworkUtility.getDepthFirstIterable(network);

		double distance=Double.MAX_VALUE;
		for (final PacketSource nextObject : children)
		{
			final int numPositions=numPositions(nextObject);

			for (int a=0;a<numPositions;a++)
			{
				if (hasParent(network, nextObject, a))
					continue;

				final Point position=getPosition(network, nextObject, a);

				final double xDistance=position.x-x;
				final double yDistance=position.y-y;

				final double newDistance=Math.sqrt(xDistance*xDistance+yDistance*yDistance);

				if (newDistance<distance)
				{
					distance=newDistance;
					answer.index=a;
					answer.object=nextObject;
				}
			}
		}

		if (answer.object==null || -1==answer.index)
			return null;

		return answer;
	}

	public static Dimension getUnzoomedPreferredSize(final NetworkContext context)
	{
		final JComponent view=context.networkView;

		final Iterable<PacketSource> iterable=NetworkUtility.getDepthFirstIterable(context.network);

		final Dimension visibleSize=view.getVisibleRect().getSize();

		int maximumX=visibleSize.width;
		int maximumY=visibleSize.height;

		for (final PacketSource component : iterable)
		{
			final int numPositions=numPositions(component);

			for (int a=0;a<numPositions;a++)
			{
				final Point position=getPosition(context.network, component, a);

				if (position.x>maximumX)
					maximumX=(int)position.x;

				if (position.y>maximumY)
					maximumY=(int)position.y;
			}
		}

		return new Dimension(maximumX, maximumY);
	}

	public static Dimension getPreferredSizeWithBuffer(final NetworkContext context, final int buffer)
	{
		final Iterable<PacketSource> iterable=NetworkUtility.getDepthFirstIterable(context.network);

		final double zoomLevel=context.zoomLevel;
		int maximumX=0;
		int maximumY=0;

		for (final PacketSource component : iterable)
		{
			final int numPositions=numPositions(component);

			for (int a=0;a<numPositions;a++)
			{
				final Point position=getPosition(context.network, component, a);

				if (position.x+buffer>maximumX)
					maximumX=(int)(position.x+buffer);

				if (position.y+buffer>maximumY)
					maximumY=(int)(position.y+buffer);
			}
		}

		return new Dimension((int)(maximumX*zoomLevel), (int)(maximumY*zoomLevel));
	}

	public static Dimension getPreferredSize(final NetworkContext context)
	{
		return getPreferredSizeWithBuffer(context, 200);
	}

	public static void revalidate(final NetworkContext context)
	{
		final Component view=context.networkView;
		view.setPreferredSize(getPreferredSize(context));
		view.invalidate();
		final JScrollPane pane=Caster.asJScrollPane(SwingUtilities.getAncestorOfClass(JScrollPane.class, view));

		if (pane!=null)
			pane.validate();

		view.repaint();
	}

}