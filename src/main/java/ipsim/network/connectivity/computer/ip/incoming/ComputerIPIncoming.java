package ipsim.network.connectivity.computer.ip.incoming;

import fpeas.maybe.MaybeUtility;
import static ipsim.Caster.equalT;
import static ipsim.Globals.DEFAULT_TIME_TO_LIVE;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asCard;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asComputer;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.isCard;
import ipsim.ExceptionHandler;
import ipsim.network.Network;
import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketUtility2;
import static ipsim.network.connectivity.PacketUtility2.isIPPacket;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import static ipsim.network.connectivity.computer.RoutingTableUtility.hasRouteFor;
import ipsim.network.connectivity.icmp.IcmpData;
import ipsim.network.connectivity.icmp.ping.PingData;
import static ipsim.network.connectivity.icmp.ping.PingData.REPLY;
import ipsim.network.connectivity.icmp.ttl.TimeExceededData;
import ipsim.network.connectivity.icmp.unreachable.UnreachableData;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.IPPacket;
import ipsim.network.connectivity.ip.SourceIPAddress;
import ipsim.network.ethernet.CardUtility;
import static ipsim.network.ethernet.ComputerUtility.cardsWithDrivers;
import ipsim.network.ethernet.NetBlock;
import static ipsim.network.ethernet.NetBlockUtility.getBroadcastAddress;
import static ipsim.network.ip.IPAddressUtility.sourceToDest;
import ipsim.util.Collections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ComputerIPIncoming implements IncomingPacketListener
{
	private final Network network;

	public ComputerIPIncoming(final Network network)
	{
		this.network=network;
	}

	@Override
    public void packetIncoming(final Packet packet, final PacketSource source, final PacketSource destination)
	{
		final IPPacket ipPacket=MaybeUtility.asNullable(PacketUtility2.asIPPacket(packet));

		@Nullable
		final Computer computer=asComputer(destination);

		@Nullable
		final Card card=asCard(source);

		if (handleBroadcastPing(computer, ipPacket))
			return;

		if (card == null)
		{
			ExceptionHandler.impossible();
			return;
		}

		if (handlePing(computer, card.withDrivers, ipPacket))
			return;

		handleForwarding(computer, card.withDrivers, ipPacket);
	}

	private boolean handleBroadcastPing(final Computer computer, final IPPacket ipPacket)
	{
		if (!equalT(ipPacket.data, PingData.REQUEST))
			return false;

		final boolean[] result={false};

		for (final CardDrivers card: cardsWithDrivers(computer))
		{
			if (result[0])
				continue;

			final NetBlock netBlock=CardUtility.getNetBlock(card);

			final IPAddress broadcastAddress=getBroadcastAddress(netBlock);
			if (equalT(ipPacket.destinationIPAddress.getIPAddress(), broadcastAddress))
			{
				final IPPacket reply=new IPPacket(new SourceIPAddress(card.ipAddress.get()), sourceToDest(ipPacket.sourceIPAddress), DEFAULT_TIME_TO_LIVE,ipPacket.identifier, REPLY);

				network.packetQueue.enqueueOutgoingPacket(reply, computer);

				return true;
			}
		}

		return result[0];
	}

	private void handleForwarding(final Computer computer, final CardDrivers card, final IPPacket ipPacket)
	{
		if (!computer.ipForwardingEnabled)
			return;

		final DestIPAddress destIP=ipPacket.destinationIPAddress;

		if (equalT(card.ipAddress.get(), destIP.getIPAddress()))
			return;

		for (final CardDrivers innerCard: cardsWithDrivers(computer))
		{
			final IPAddress broadcastAddress=getBroadcastAddress(CardUtility.getNetBlock(innerCard));

			if (equalT(ipPacket.destinationIPAddress.getIPAddress(), broadcastAddress))
			{
				handleBroadcastPing(computer, ipPacket);
				return;
			}
		}

		final boolean hasRoute=hasRouteFor(computer, destIP);

		final IPPacket newIPPacket;

		if (hasRoute)
		{
			if (ipPacket.timeToLive<2)
				newIPPacket=dropPacket(card, ipPacket, TimeExceededData.TIME_TO_LIVE_EXCEEDED);
			else
				newIPPacket=forwardPacket(ipPacket);

			network.packetQueue.enqueueOutgoingPacket(newIPPacket, computer);
		}
		else
		{
			newIPPacket=dropPacket(card, ipPacket, UnreachableData.NET_UNREACHABLE);

			network.packetQueue.enqueueOutgoingPacket(newIPPacket, computer);
		}
	}

	private static IPPacket dropPacket(final CardDrivers card, final IPPacket ipPacket, final IcmpData reason)
	{
		return new IPPacket
			(
				new SourceIPAddress(card.ipAddress.get()),
				new DestIPAddress(ipPacket.sourceIPAddress.getIPAddress()),
				DEFAULT_TIME_TO_LIVE,
				ipPacket.identifier,
				reason
			);
	}

	private static IPPacket forwardPacket(final IPPacket ipPacket)
	{
		return new IPPacket
			(
				ipPacket.sourceIPAddress,
				ipPacket.destinationIPAddress,
				ipPacket.timeToLive-1,
				ipPacket.identifier,
				ipPacket.data
			);
	}

	private boolean handlePing(final Computer computer, @NotNull final CardDrivers card, final IPPacket ipPacket)
	{
		if (!equalT(ipPacket.data, PingData.REQUEST))
			return false;

		final List<CardDrivers> cards;

		if (computer.ipForwardingEnabled)
			cards=cardsWithDrivers(computer);
		else
		{
			cards=Collections.arrayList();
			cards.add(card);
		}

		for (final CardDrivers innerCard: cards)
		{
			if (equalT(innerCard.ipAddress.get(), ipPacket.destinationIPAddress.getIPAddress()))
			{
				final IPPacket reply=new IPPacket(new SourceIPAddress(innerCard.ipAddress.get()), sourceToDest(ipPacket.sourceIPAddress), DEFAULT_TIME_TO_LIVE,ipPacket.identifier, REPLY);

				network.packetQueue.enqueueOutgoingPacket(reply, computer);

				return true;
			}
		}

		return false;
	}

	@Override
    public boolean canHandle(final Packet packet, final PacketSource source)
	{
		if (!isIPPacket(packet))
			return false;

		return isCard(source);
	}
}