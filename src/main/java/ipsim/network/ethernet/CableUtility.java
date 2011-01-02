package ipsim.network.ethernet;

import ipsim.network.Network;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import org.jetbrains.annotations.Nullable;

import static ipsim.Caster.equalT;
import static ipsim.gui.PositionUtility.getParent;

public final class CableUtility {
    private CableUtility() {
    }

    public static PacketSource getOtherEnd(final Network network, final Cable cable, final PacketSource source) throws OnlyOneEndConnectedException {
        @Nullable
        final PacketSource parent0 = getParent(network, cable, 0);

        @Nullable
        final PacketSource parent1 = getParent(network, cable, 1);

        if (parent0 != null && parent1 != null) {
            if (!equalT(source, parent0) && !equalT(source, parent1))
                throw new IllegalArgumentException(source + " is not attached to " + cable);

            return equalT(parent0, source) ? parent1 : parent0;
        }

        throw new OnlyOneEndConnectedException("Only one end is connected");
    }
}