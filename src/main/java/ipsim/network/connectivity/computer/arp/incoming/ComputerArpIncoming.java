package ipsim.network.connectivity.computer.arp.incoming;

import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asCard;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asComputer;
import ipsim.ExceptionHandler;
import static ipsim.lang.Assertion.assertTrue;
import ipsim.lang.CheckedIllegalStateException;
import ipsim.network.Network;
import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketUtility;
import ipsim.network.connectivity.PacketUtility2;
import ipsim.network.connectivity.arp.ArpPacket;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.ethernet.CardUtility;
import ipsim.network.ethernet.ComputerUtility;
import org.jetbrains.annotations.Nullable;

public final class ComputerArpIncoming implements IncomingPacketListener
{
	private final Network network;

	public ComputerArpIncoming(final Network network)
	{
		this.network=network;
	}

	@Override
    public void packetIncoming(final Packet packet, final PacketSource source, final PacketSource destination)
	{
		assertTrue(PacketSourceUtility.isCard(source));

		@Nullable
		final Computer computer;
		final ArpPacket arpPacket;
		final @Nullable Card card;

		try
		{
			arpPacket=PacketUtility.asArpPacket(packet);
			computer=asComputer(destination);
			card=asCard(source);
		}
		catch (final CheckedIllegalStateException exception)
		{
			ExceptionHandler.impossible();
			return;
		}

		if (card==null || computer==null)
		{
			ExceptionHandler.impossible();
			return;
		}

		if (!card.hasDeviceDrivers())
			return;

		if (CardUtility.isOnSameSubnet(card, arpPacket.sourceIPAddress))
			computer.arpTable.put(arpPacket.sourceIPAddress, arpPacket.sourceMacAddress, network);

		if (0==arpPacket.destinationMacAddress.rawValue)
			for (final CardDrivers card2: ComputerUtility.cardsWithDrivers(computer))
				if (card2.ipAddress.get().rawValue==arpPacket.destinationIPAddress.rawValue)
					network.packetQueue.enqueueOutgoingPacket(new ArpPacket(arpPacket.sourceIPAddress, arpPacket.sourceMacAddress, card2.ipAddress.get(), card2.card.getMacAddress(network), arpPacket.id), computer);
	}

	@Override
    public boolean canHandle(final Packet packet, final PacketSource source)
	{
		return PacketUtility2.isArpPacket(packet);
	}
}