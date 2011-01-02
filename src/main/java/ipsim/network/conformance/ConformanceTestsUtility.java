package ipsim.network.conformance;

import fj.F;
import fj.data.Option;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.ethernet.ComputerUtility;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.NonsensicalArrangement.cableWithEndsDisconnected;
import static ipsim.network.conformance.NonsensicalArrangement.cardWithZeroIP;
import static ipsim.network.conformance.NonsensicalArrangement.cardWithoutCable;
import static ipsim.network.conformance.NonsensicalArrangement.cardWithoutDeviceDrivers;
import static ipsim.network.conformance.NonsensicalArrangement.computerWithoutNetworkCard;
import static ipsim.network.conformance.NonsensicalArrangement.customCheck;
import static ipsim.network.conformance.NonsensicalArrangement.hubWithNoCables;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.RoutePointsAtNonLocalGateway.routePointsAtNonLocalGateway;
import static ipsim.network.conformance.TypicalScores.USUAL;
import static ipsim.util.Collections.arrayList;

public class ConformanceTestsUtility {
    public static Collection<F<Network, CheckResult>> createNetworkCheck() {
        final Collection<F<Network, CheckResult>> checks = arrayList();

        checks.add(new PercentOfIPsMatchingProblem());
        checks.add(new CycleInDefaultRoutes());
        checks.add(new ZeroSubnetMaskUsed());
        checks.add(new OneSubnetMaskUsed());
        checks.add(new PercentOfRequiredSubnets());

        checks.add(computerWithoutNetworkCard());
        checks.add(cardWithoutCable());
        checks.add(cableWithEndsDisconnected());
        checks.add(cardWithoutDeviceDrivers());
        checks.add(cardWithZeroIP());
        checks.add(hubWithNoCables());

        checks.add(new PercentUniqueIPAddresses());
        checks.add(new HubWithMoreThanOneSubnet());
        checks.add(new SomeHostsHaveMoreThanThreeCards());

        checks.add(new ZeroHostNumberUsed());

        checks.add(new OneHostNumberUsed());

        checks.add(new SomeHubsHaveNoStandaloneHost());

        checks.add(new SomeHostsHaveABadNetMask());

        checks.add(new SomeHostsHaveTwoCardsWithTheSameNetworkNumber());

        checks.add(new SomeHostsHaveRouteToSelf());

        checks.add(new SomeHostsHaveMoreThanOneDefaultRoute());

        checks.add(new ExplicitRouteOnNonGateway());

        checks.add(new SomeRoutesToNonGateways());

        checks.add(new ExplicitRouteToLocalNetwork());

        checks.add(new NonGatewayWithoutDefaultRoute());

        checks.add(new PacketForwardingEnabledOnNonGateway());

        checks.add(new SomeHostsHaveMoreThanOneRouteToTheSamePlace());
        checks.add(new MultipleRoutesToTheSameSubnet());
        checks.add(routePointsAtNonLocalGateway);

        return checks;
    }

    public static CheckResult someHostsHaveThisManyCards(final Network network, final String string, final F<Integer, Boolean> thisMany) {
        final F<Computer, Option<String>> haveThisMany = new F<Computer, Option<String>>() {
            @Override
            @NotNull
            public Option<String> f(@NotNull final Computer computer) {
                return thisMany.f(computer.getCards().size()) ? Option.some("A computer with " + string + " cards") : Option.<String>none();
            }
        };

        final F<Computer, Option<String>> noErrors = noErrors();

        return customCheck(getAllComputers, haveThisMany, noErrors, USUAL).f(network);
    }

    public static F<Computer, Boolean> isARouter() {
        return new F<Computer, Boolean>() {
            @Override
            @NotNull
            public Boolean f(@NotNull final Computer computer) {
                return ComputerUtility.getIPAddresses(computer).size() > 1;
            }
        };
    }

}