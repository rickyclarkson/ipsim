package ipsim.persistence.delegates;

import ipsim.Caster;
import ipsim.awt.Point;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.readFromDeserialiser;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.writePacketSource;
import static ipsim.gui.PositionUtility.getParent;
import static ipsim.gui.PositionUtility.getPosition;
import static ipsim.gui.PositionUtility.numPositions;
import static ipsim.gui.PositionUtility.setParent;
import static ipsim.gui.PositionUtility.setPosition;
import ipsim.network.Network;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketSourceAndIndex;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import static ipsim.persistence.delegates.PointDelegate.pointDelegate;
import static ipsim.util.Collections.mapWith;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

public final class DelegateHelper
{
	private DelegateHelper()
	{
	}

	public static void writePositions(final Network network,final XMLSerialiser serialiser,final PacketSource component)
	{
		int a=0;

		for (final PacketSourceAndIndex childHandler: component.children())
			writePacketSource(serialiser,childHandler.packetSource, "child "+a++,network);

		for (a=0;a<numPositions(component);a++)
		{
			@Nullable
			final PacketSource parent=getParent(network, component, a);

			if (parent!=null)
				writePacketSource(serialiser,parent,"parent "+a,network);
			else
				serialiser.writeObject(getPosition(network,component,a),"point "+a, pointDelegate);
		}
	}

	public static void readPositions(final Network network, final XMLDeserialiser deserialiser, final Node node, final PacketSource component)
	{
		final String[] nodeNames=deserialiser.getObjectNames(node);

		for (final String nodeName: nodeNames)
		{
			if (nodeName.startsWith("parent "))
			{
				final int a=Integer.parseInt(nodeName.substring("parent ".length()));

				setParent(network,component,a, readFromDeserialiser(deserialiser,node,nodeName,network),0);
			}

			if (nodeName.startsWith("point "))
			{
				final String string=nodeName.substring("point ".length());

				final int a=Integer.parseInt(string);

				setPosition(network,component, mapWith(a,deserialiser.readObject(node,nodeName, pointDelegate, Caster.asFunction(Point.class))));
			}

			if (nodeName.startsWith("child "))
				readFromDeserialiser(deserialiser,node,nodeName,network);
		}
	}
}