package ipsim.network.connectivity.computer;

import ipsim.lang.Stringable;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ethernet.NetBlock;

public final class Route implements Stringable {
    public final NetBlock block;
    public final IPAddress gateway;

    public Route(final NetBlock block, final IPAddress gateway) {
        this.block = block;
        this.gateway = gateway;
    }

    @Override
    public String asString() {
        final boolean bool = 0 == block.networkNumber.rawValue || 0 == block.netMask.rawValue;
        final String string = bool ? "default" : block.asString();

        return "Destination: " + string + " Gateway: " + (0 == gateway.rawValue ? "default" : gateway.asString());
    }

    @Override
    public String toString() {
        return asString();
    }
}
