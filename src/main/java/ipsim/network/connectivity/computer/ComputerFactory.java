package ipsim.network.connectivity.computer;

import ipsim.awt.Point;
import ipsim.gui.PacketSourceAndPoints;
import ipsim.network.Network;
import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.Listeners;
import ipsim.network.connectivity.OutgoingPacketListener;
import ipsim.network.connectivity.computer.arp.incoming.ComputerArpIncoming;
import ipsim.network.connectivity.computer.arp.outgoing.ComputerArpOutgoing;
import ipsim.network.connectivity.computer.ethernet.incoming.ComputerEthernetIncoming;
import ipsim.network.connectivity.computer.ethernet.outgoing.ComputerEthernetOutgoing;
import ipsim.network.connectivity.computer.ip.incoming.ComputerIPIncoming;
import ipsim.network.connectivity.computer.ip.outgoing.ComputerIPOutgoing;
import static ipsim.util.Collections.hashMap;

import java.util.Map;

public final class ComputerFactory
{
	public static Computer newComputer(final Network network, final int x, final int y)
	{
		final Computer computer=new Computer();
		final Map<Integer, Point> map=hashMap();
		map.put(0,new Point(x,y));
		network.topLevelComponents.add(new PacketSourceAndPoints(computer,map));

		final Listeners<OutgoingPacketListener> outgoingPacketListeners=computer.getOutgoingPacketListeners();

		outgoingPacketListeners.add(new ComputerEthernetOutgoing(network));
		outgoingPacketListeners.add(new ComputerArpOutgoing(network));
		outgoingPacketListeners.add(new ComputerIPOutgoing(network));

		final Listeners<IncomingPacketListener> incomingPacketListeners=computer.getIncomingPacketListeners();

		incomingPacketListeners.add(new ComputerEthernetIncoming(network));
		incomingPacketListeners.add(new ComputerArpIncoming(network));
		incomingPacketListeners.add(new ComputerIPIncoming(network));

		return computer;
	}

	public static Computer newComputerWithID(final Network network, final int x, final int y)
	{
		final Computer computer=newComputer(network,x,y);
		computer.computerID=network.generateComputerID();
		return computer;
	}
}