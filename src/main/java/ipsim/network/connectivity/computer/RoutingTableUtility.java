package ipsim.network.connectivity.computer;

import fj.F;
import fj.data.IterableW;
import ipsim.lang.FunctionUtility;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ethernet.CardUtility;
import ipsim.network.ethernet.ComputerUtility;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.RouteUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ipsim.lang.Assertion.assertNotNull;

public class RoutingTableUtility {
    /**
     * TODO think about how to choose between two equal card-generated routes.  See email to AJY dated 25-Nov-2006.
     * Current implementation chooses the first it finds.
     */
    @Nullable
    private static Route implGetRouteFor(final Computer computer, final DestIPAddress destIP) {
        for (final CardDrivers card : ComputerUtility.cardsWithDrivers(computer)) {
            final NetBlock block = CardUtility.getNetBlock(card);
            final IPAddress cardIP = card.ipAddress.get();

            if (0 == cardIP.rawValue)
                continue;

            if (block.networkContains(destIP.getIPAddress()))
                return new Route(block, cardIP);
        }

        final RoutingTable routingTable = computer.routingTable;

        for (final Route entry : routingTable.routes()) {
            final NetBlock destination = entry.block;

            if (destination.networkContains(destIP.getIPAddress()))
                return entry;
        }

        return null;
    }

    public static boolean hasRouteFor(final Computer computer, final DestIPAddress destIP) {
        assertNotNull(destIP);

        return implGetRouteFor(computer, destIP) != null;
    }

    public static Route getRouteFor(final Computer computer, final DestIPAddress destIP) throws NoSuchRouteException {
        final Route route = implGetRouteFor(computer, destIP);

        if (route != null)
            return route;

        throw new NoSuchRouteException();
    }

    public static RoutingTable createRoutingTable() {
        return new RoutingTable();
    }

    public static final F<Route, Boolean> isDefaultRoute = new F<Route, Boolean>() {
        @Override
        @NotNull
        public Boolean f(@NotNull final Route route) {
            return RouteUtility.isDefaultRoute(route);
        }
    };

    public static Iterable<Route> getExplicitRoutes(final RoutingTable table) {
        return IterableW.wrap(table.routes()).filter(FunctionUtility.not(isDefaultRoute));
    }

    public static Iterable<Route> getDefaultRoutes(final RoutingTable table) {
        return IterableW.wrap(table.routes()).filter(isDefaultRoute);
    }
}