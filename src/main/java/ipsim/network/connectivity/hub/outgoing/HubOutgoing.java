package ipsim.network.connectivity.hub.outgoing;

import ipsim.network.connectivity.OutgoingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketQueue;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import ipsim.network.connectivity.hub.Hub;
import org.jetbrains.annotations.NotNull;

import static ipsim.Caster.asNotNull;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asHub;
import static ipsim.network.connectivity.PacketUtility2.asEthernetPacket;

public class HubOutgoing implements OutgoingPacketListener {
    private final PacketQueue queue;

    public HubOutgoing(final PacketQueue queue) {
        this.queue = queue;
    }

    @Override
    public void packetOutgoing(final Packet packet, final PacketSource source) {
        packetOutgoingImpl(asNotNull(asEthernetPacket(packet)), asNotNull(asHub(source)));
    }

    private void packetOutgoingImpl(final @NotNull EthernetPacket packet, final @NotNull Hub source) {
        for (final Cable cable : source.getCables())
            queue.enqueueIncomingPacket(packet, source, cable);
    }

    @Override
    public boolean canHandle(final Packet packet, final PacketSource source) {
        return true;
    }
}