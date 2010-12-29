package ipsim.network.connectivity.cable.incoming;

import fpeas.predicate.PredicateUtility;
import static ipsim.Caster.asNotNull;
import static ipsim.Caster.equalT;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asCable;
import ipsim.network.Network;
import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketSource;
import static ipsim.network.connectivity.PacketUtility2.asEthernetPacket;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import ipsim.util.Collections;
import org.jetbrains.annotations.NotNull;

public class CableIncoming implements IncomingPacketListener
{
	private final Network network;

	public CableIncoming(final Network network)
	{
		this.network=network;
	}

	@Override
    public void packetIncoming(final Packet packet, final PacketSource source, final PacketSource destination)
	{
		packetIncomingImpl(network,asNotNull(asEthernetPacket(packet)),source,asNotNull(asCable(destination)));
	}

	public static void packetIncomingImpl(@NotNull final Network network,@NotNull final EthernetPacket packet, @NotNull final PacketSource source, @NotNull final Cable destination)
	{
		final boolean canTransferPackets=destination.canTransferPackets(network);

		if (!canTransferPackets)
			return;

		final Iterable<PacketSource> ends=Collections.only(destination.getEnds(network), PredicateUtility.not(equalT(source)));

		network.packetQueue.enqueueIncomingPacket(packet,destination, ends.iterator().next());
	}

	@Override
	public String toString()
	{
		throw new UnsupportedOperationException();
	}

	@Override
    public boolean canHandle(final Packet packet, final PacketSource source)
	{
		return true;
	}
}