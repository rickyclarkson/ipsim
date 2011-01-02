package ipsim.network.conformance;

import fj.F;
import fj.data.Option;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.ConformanceTestsUtility.isARouter;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;

final class PacketForwardingEnabledOnNonGateway extends F<Network, CheckResult> {
    @Override
    @NotNull
    public CheckResult f(@NotNull final Network network) {
        final F<Computer, Option<String>> noErrors = noErrors(), warning = new F<Computer, Option<String>>() {
            @Override
            @NotNull
            public Option<String> f(@NotNull final Computer computer) {
                return !isARouter().f(computer) && computer.ipForwardingEnabled ? Option.some("Computer that is not a gateway but has packet forwarding enabled") : Option.<String>none();
            }
        };

        return NonsensicalArrangement.customCheck(getAllComputers, warning, noErrors, USUAL).f(network);
    }
}