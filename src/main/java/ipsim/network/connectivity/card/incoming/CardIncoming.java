package ipsim.network.connectivity.card.incoming;

import ipsim.network.Network;
import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import org.jetbrains.annotations.NotNull;

import static ipsim.Caster.asNotNull;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asCable;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asCard;
import static ipsim.gui.PositionUtility.getParent;
import static ipsim.lang.Assertion.assertNotNull;
import static ipsim.network.connectivity.PacketUtility2.asEthernetPacket;

public final class CardIncoming implements IncomingPacketListener {
    private final Network network;

    public CardIncoming(final Network network) {
        this.network = network;
    }

    @Override
    public void packetIncoming(final Packet packet, final PacketSource source, final PacketSource destination) {
        packetIncomingImpl(network, asNotNull(asEthernetPacket(packet)), asNotNull(asCable(source)), asNotNull(asCard(destination)));
    }

    public static void packetIncomingImpl(final Network network, @NotNull final EthernetPacket packet, @NotNull final PacketSource source, @NotNull final Card destination) {
        assertNotNull(packet, source, destination);

        if (destination.hasDeviceDrivers())
            network.packetQueue.enqueueIncomingPacket(packet, destination, asNotNull(getParent(network, destination, 0)));
    }

    @Override
    public boolean canHandle(final Packet packet, final PacketSource source) {
        return true;
    }
}