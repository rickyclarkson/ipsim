package ipsim.network.conformance;

import fj.F;
import fj.data.Option;
import ipsim.network.Network;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.NoDeviceDriversException;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.util.Collections;
import org.jetbrains.annotations.NotNull;

import static ipsim.Caster.equalT;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getExplicitRoutes;
import static ipsim.network.ethernet.CardUtility.getNetBlock;

class ExplicitRouteToLocalNetwork extends F<Network, CheckResult> {
    @Override
    @NotNull
    public CheckResult f(@NotNull final Network network) {
        final F<Computer, Option<String>> warning = new F<Computer, Option<String>>() {
            @Override
            @NotNull
            public Option<String> f(@NotNull final Computer computer) {
                return Collections.any(computer.getCards(), new F<Card, Boolean>() {
                    @Override
                    public Boolean f(final Card card) {
                        for (final Route route : getExplicitRoutes(computer.routingTable))
                            try {
                                if (equalT(route.block, getNetBlock(card)))
                                    return true;
                            } catch (NoDeviceDriversException ignored) {
                            }

                        return false;
                    }

                }) ? Option.some("Computer with an explicit route that points to one of its local networks") : Option.<String>none();
            }
        };

        final F<Computer, Option<String>> noErrors = noErrors();

        return NonsensicalArrangement.customCheck(getAllComputers, warning, noErrors, USUAL).f(network);
    }
}