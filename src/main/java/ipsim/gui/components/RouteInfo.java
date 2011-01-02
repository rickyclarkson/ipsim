package ipsim.gui.components;

import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ethernet.NetBlock;

import static ipsim.network.ethernet.NetBlockUtility.asCustomString;

public final class RouteInfo {
    public final NetBlock destination;
    public final IPAddress gateway;

    public RouteInfo(final NetBlock destination, final IPAddress gateway) {
        this.destination = destination;
        this.gateway = gateway;
    }

    /*
      * Copy of the one from RouteUtilityImplementation
      */
    public String asString() {
        final boolean bool = 0 == destination.networkNumber.rawValue || 0 == destination.netMask.rawValue;
        final String string = bool ? "default" : asCustomString(destination);

        return "Destination: " + string + " Gateway: " + (0 == gateway.rawValue ? "default" : gateway.asString());
    }
}