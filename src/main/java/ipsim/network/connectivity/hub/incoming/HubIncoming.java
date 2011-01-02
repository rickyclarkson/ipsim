package ipsim.network.connectivity.hub.incoming;

import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketQueue;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import ipsim.network.connectivity.hub.Hub;
import ipsim.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import static ipsim.Caster.asNotNull;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asCable;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asHub;
import static ipsim.network.connectivity.PacketUtility2.asEthernetPacket;

public class HubIncoming implements IncomingPacketListener {
    private final PacketQueue queue;

    public HubIncoming(final PacketQueue queue) {
        this.queue = queue;
    }

    @Override
    public void packetIncoming(final Packet packet, final PacketSource source, final PacketSource destination) {
        packetIncomingImpl(asNotNull(asEthernetPacket(packet)), asNotNull(asCable(source)), asNotNull(asHub(destination)));
    }

    private void packetIncomingImpl(@NotNull final EthernetPacket packet, @NotNull final Cable source, @NotNull final Hub hub) {
        if (!hub.isPowerOn())
            return;

        final List<Cable> cables = Collections.arrayList();

        cables.addAll(hub.getCables());

        cables.remove(source);

        for (final Cable cable : cables)
            queue.enqueueIncomingPacket(packet, hub, cable);
    }

    @Override
    public boolean canHandle(final Packet packet, final PacketSource source) {
        return true;
    }
}