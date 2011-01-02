package ipsim.network.connectivity.cable;

import ipsim.awt.Point;
import ipsim.network.Network;
import ipsim.network.connectivity.cable.incoming.CableIncoming;
import ipsim.network.connectivity.cable.outgoing.CableOutgoing;
import java.util.HashMap;

import static ipsim.gui.PositionUtility.setPosition;

public final class CableFactory {
    private final Network network;

    public CableFactory(final Network network) {
        this.network = network;
    }

    private static void initialise(final Network network, final Cable cable) {
        cable.getIncomingPacketListeners().add(new CableIncoming(network));
        cable.getOutgoingPacketListeners().add(new CableOutgoing(network));
    }

    public Cable newCable(final int x1, final int y1, final int x2, final int y2) {
        final Cable cable = new Cable();
        setPosition(network, cable, new HashMap<Integer, Point>() {{
            put(0, new Point(x1, y1));
            put(1, new Point(x2, y2));
        }});

        initialise(network, cable);

        return cable;
    }
}