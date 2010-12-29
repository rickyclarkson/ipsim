package ipsim.network.connectivity.cable.outgoing;

import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asCable;
import static ipsim.ExceptionHandler.impossible;
import ipsim.network.Network;
import ipsim.network.connectivity.OutgoingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketUtility2;
import ipsim.network.connectivity.cable.Cable;

public final class CableOutgoing implements OutgoingPacketListener
{
	private final Network network;

	public CableOutgoing(final Network network)
	{
		this.network=network;
	}

	@Override
    public void packetOutgoing(final Packet packet,final PacketSource source)
	{
		final Cable cable=asCable(source);

		if (cable==null)
		{
			impossible();
			return;
		}

		for (final PacketSource end: cable.getEnds(network))
			network.packetQueue.enqueueIncomingPacket(packet,source,end);
	}

	@Override
    public boolean canHandle(final Packet packet,final PacketSource source)
	{
		return PacketUtility2.isEthernetPacket(packet);
	}
}