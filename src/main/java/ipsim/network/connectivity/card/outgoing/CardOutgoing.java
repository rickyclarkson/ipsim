package ipsim.network.connectivity.card.outgoing;

import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.ExceptionHandler;
import static ipsim.lang.Assertion.assertTrue;
import ipsim.network.Network;
import ipsim.network.connectivity.OutgoingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketSource;
import static ipsim.network.connectivity.PacketUtility2.isEthernetPacket;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import org.jetbrains.annotations.Nullable;

public final class CardOutgoing implements OutgoingPacketListener
{
	private final Network network;

	public CardOutgoing(final Network network)
	{
		this.network=network;
	}

	@Override
    public void packetOutgoing(final Packet packet, final PacketSource source)
	{
		assertTrue(canHandle(packet, source));

		final @Nullable Card card=PacketSourceUtility.asCard(source);

		if (card==null)
		{
			ExceptionHandler.impossible();
			return;
		}

		@Nullable
		final Cable cable=card.getCable();

		if (cable==null)
			return;

		network.packetQueue.enqueueIncomingPacket(packet, card, cable);
	}

	@Override
	public String toString()
	{
		return "CardOutgoing";
	}

	@Override
    public boolean canHandle(final Packet packet, final PacketSource source)
	{
		final @Nullable Card maybeCard=PacketSourceUtility.asCard(source);

		if (maybeCard == null)
			return ExceptionHandler.<Boolean>impossible();

		return isEthernetPacket(packet) && maybeCard.hasCable();
	}
}