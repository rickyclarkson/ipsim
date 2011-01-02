package ipsim.network.conformance;

import fj.F;
import fj.data.Option;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.ConformanceTestsUtility.isARouter;
import static ipsim.network.conformance.TypicalScores.USUAL;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getExplicitRoutes;

class ExplicitRouteOnNonGateway extends F<Network, CheckResult> {
    @Override
    @NotNull
    public CheckResult f(@NotNull final Network network) {
        final F<Computer, Option<String>> warning = new F<Computer, Option<String>>() {
            @Override
            @NotNull
            public Option<String> f(@NotNull final Computer computer) {
                return getExplicitRoutes(computer.routingTable).iterator().hasNext() && !isARouter().f(computer) ? Option.some("An explicit route on a computer that is not a gateway") : Option.<String>none();
            }

        };

        final F<Computer, Option<String>> noErrors = NonsensicalArrangement.noErrors();
        return NonsensicalArrangement.customCheck(getAllComputers, warning, noErrors, USUAL).f(network);
    }
}