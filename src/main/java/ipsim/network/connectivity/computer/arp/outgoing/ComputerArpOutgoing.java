package ipsim.network.connectivity.computer.arp.outgoing;

import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asComputer;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import static ipsim.lang.Assertion.assertTrue;
import ipsim.lang.CheckedIllegalStateException;
import ipsim.network.Network;
import ipsim.network.connectivity.OutgoingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketSource;
import static ipsim.network.connectivity.PacketUtility.asArpPacket;
import ipsim.network.connectivity.PacketUtility2;
import ipsim.network.connectivity.arp.ArpPacket;
import static ipsim.network.connectivity.arp.ArpPacketUtility.isRequest;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ethernet.EthernetPacket;

public final class ComputerArpOutgoing implements OutgoingPacketListener
{
	private final Network network;

	public ComputerArpOutgoing(final Network network)
	{
		this.network=network;
	}

	@Override
    public void packetOutgoing(final Packet packet,final PacketSource source)
	{
		assertTrue(canHandle(packet,source));

		final ArpPacket arpPacket;
		final Computer computer;
		try
		{
			arpPacket=asArpPacket(packet);
			computer=asComputer(source);
		}
		catch (final CheckedIllegalStateException exception)
		{
			throw new RuntimeException(exception);
		}

		if (isRequest(arpPacket) && !computer.arpTable.hasEntryFor(arpPacket.destinationIPAddress))
			computer.arpTable.putIncomplete(arpPacket.destinationIPAddress, network);

		final EthernetPacket ethPacket=new EthernetPacket(arpPacket.sourceMacAddress, arpPacket.destinationMacAddress, arpPacket);

		network.packetQueue.enqueueOutgoingPacket(ethPacket,source);
	}

	@Override
    public boolean canHandle(final Packet packet,final PacketSource source)
	{
		final boolean isAComputer=PacketSourceUtility.isComputer(source);
		return isAComputer&&PacketUtility2.isArpPacket(packet);
	}
}