package ipsim.network.connectivity.computer.ip.outgoing;

import static ipsim.Caster.equalT;
import static ipsim.lang.Assertion.assertFalse;
import ipsim.lang.CheckedIllegalStateException;
import ipsim.network.Network;
import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketQueue;
import ipsim.network.connectivity.PacketSource;
import static ipsim.network.connectivity.PacketUtility.asArpPacket;
import ipsim.network.connectivity.PacketUtility2;
import ipsim.network.connectivity.arp.ArpPacket;
import ipsim.network.connectivity.ethernet.MacAddress;

public final class ContinueArpPacketListener implements IncomingPacketListener
{
	private final Network network;

	private final Packet originalPacket;

	private boolean dead=false;

	private final Object requestId;

	public ContinueArpPacketListener(final Network network,final Packet originalPacket,final Object requestId)
	{
		this.network=network;
		this.originalPacket=originalPacket;
		this.requestId=requestId;
	}

	@Override
    public void packetIncoming(final Packet packet,final PacketSource source,final PacketSource destination)
	{
		assertFalse(dead);

		if (!PacketUtility2.isArpPacket(packet))
			return;

		final ArpPacket arpPacket;
		try
		{
			arpPacket=asArpPacket(packet);
		}
		catch (final CheckedIllegalStateException exception)
		{
			throw new RuntimeException(exception);
		}

		if (!equalT(arpPacket.id, requestId))
			return;

		if (equalT(arpPacket.destinationMacAddress, new MacAddress(0)))
			return;

		final PacketQueue queue=network.packetQueue;
		destination.getIncomingPacketListeners().remove(this);

		queue.enqueueOutgoingPacket(originalPacket,destination);
		dead=true;
	}

	@Override
    public boolean canHandle(final Packet packet, final PacketSource source)
	{
		if (dead)
			return false;

		try
		{
			final ArpPacket arpPacket=asArpPacket(packet);
			return !(0==arpPacket.destinationMacAddress.rawValue);
		}
		catch (final CheckedIllegalStateException exception)
		{
			return false;
		}
	}
}