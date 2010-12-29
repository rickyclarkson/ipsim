package ipsim.network.connectivity.computer.ethernet.incoming;

import fpeas.maybe.MaybeUtility;
import static ipsim.Caster.asNotNull;
import static ipsim.Caster.equalT;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.ExceptionHandler;
import ipsim.network.Network;
import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketUtility2;
import ipsim.network.connectivity.arp.ArpPacket;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import ipsim.network.connectivity.ethernet.MacAddress;
import ipsim.network.connectivity.ip.IPPacket;
import ipsim.network.ethernet.CardUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ComputerEthernetIncoming implements IncomingPacketListener
{
	private final Network network;

	public ComputerEthernetIncoming(final Network network)
	{
		this.network=network;
	}

	@Override
    public void packetIncoming(final Packet packet,final PacketSource source,final PacketSource destination)
	{
		final @Nullable Card maybeCard=PacketSourceUtility.asCard(source);

		if (maybeCard==null)
		{
			ExceptionHandler.impossible();
			return;
		}

		final @Nullable CardDrivers card=maybeCard.withDrivers;

		final @Nullable EthernetPacket maybePacket=PacketUtility2.asEthernetPacket(packet);

		packetIncomingImpl(network,asNotNull(card),asNotNull(maybePacket),destination);
	}

	public static void packetIncomingImpl(final Network network,@NotNull final CardDrivers card,@NotNull final EthernetPacket ethPacket,@NotNull final PacketSource destination)
	{
		final MacAddress zero=new MacAddress(0);

		final MacAddress destinationAddress=ethPacket.destinationAddress;

		if ((equalT(destinationAddress, card.card.getMacAddress(network)) || equalT(destinationAddress, zero))&& PacketUtility2.isArpPacket(ethPacket.data) && !equalT(ethPacket.sourceAddress, card.card.getMacAddress(network)))
		{
			final ArpPacket arpPacket=MaybeUtility.asNullable(PacketUtility2.asArpPacket(ethPacket.data));

			network.packetQueue.enqueueIncomingPacket(arpPacket, card.card,destination);
		}

		if (equalT(destinationAddress, card.card.getMacAddress(network)) && PacketUtility2.isIPPacket(ethPacket.data))
			network.packetQueue.enqueueIncomingPacket(MaybeUtility.asNullable(PacketUtility2.asIPPacket(ethPacket.data)), card.card,destination);

		if (equalT(destinationAddress, zero) && PacketUtility2.isIPPacket(ethPacket.data))
		{
			final IPPacket ipPacket=MaybeUtility.asNullable(PacketUtility2.asIPPacket(ethPacket.data));

			if (equalT(CardUtility.getBroadcastAddress(card), ipPacket.destinationIPAddress.getIPAddress()))
				network.packetQueue.enqueueIncomingPacket(MaybeUtility.asNullable(PacketUtility2.asIPPacket(ethPacket.data)), card.card, destination);
		}
	}

	@Override
    public boolean canHandle(final Packet packet, final PacketSource source)
	{
		return PacketUtility2.isEthernetPacket(packet);
	}
}