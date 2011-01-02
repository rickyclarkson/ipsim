package ipsim.network.ethernet;

import fj.F;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.util.Collections;

import static ipsim.Caster.equalT;
import static ipsim.network.ethernet.ComputerUtility.cardsWithDrivers;

public class RouteUtility {
    public static boolean isDefaultRoute(final Route route) {
        return equalT(route.block, NetBlockUtility.getZero());
    }

    public static boolean isRouteToSelf(final Computer computer, final Route route) {
        return Collections.any(cardsWithDrivers(computer), new F<CardDrivers, Boolean>() {
            @Override
            public Boolean f(final CardDrivers card) {
                return equalT(card.ipAddress.get(), route.gateway);
            }
        });
    }

    public static String asCustomString(final Route route) {
        final NetBlock destination = route.block;
        final boolean bool = 0 == destination.networkNumber.rawValue && 0 == destination.netMask.rawValue;
        final String string = bool ? "default" : NetBlockUtility.asCustomString(destination);

        final IPAddress gateway = route.gateway;
        return "Destination: " + string + " Gateway: " + (0 == gateway.rawValue ? "default" : gateway.asString());
    }
}