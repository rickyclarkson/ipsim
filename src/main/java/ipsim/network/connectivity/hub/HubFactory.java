package ipsim.network.connectivity.hub;

import ipsim.gui.components.HubHandler;
import ipsim.network.Network;
import ipsim.network.connectivity.PacketQueue;
import ipsim.network.connectivity.hub.incoming.HubIncoming;
import ipsim.network.connectivity.hub.outgoing.HubOutgoing;

public final class HubFactory {
    public static Hub newHub(final Network network, final int x, final int y) {
        final Hub hub = HubHandler.create(network, x, y);

        final PacketQueue queue = network.packetQueue;

        hub.getIncomingPacketListeners().add(new HubIncoming(queue));
        hub.getOutgoingPacketListeners().add(new HubOutgoing(queue));

        return hub;
    }
}