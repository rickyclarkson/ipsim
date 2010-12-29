package ipsim.network.conformance;

import fpeas.function.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.ConformanceTestsUtility.isARouter;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.computer.Computer;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getExplicitRoutes;
import org.jetbrains.annotations.NotNull;

class ExplicitRouteOnNonGateway implements Function<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult run(@NotNull final Network network)
	{
		final Function<Computer, Maybe<String>> warning=new Function<Computer, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> run(@NotNull final Computer computer)
			{
				return getExplicitRoutes(computer.routingTable).iterator().hasNext()&&!isARouter().run(computer) ? MaybeUtility.just("An explicit route on a computer that is not a gateway") : MaybeUtility.<String>nothing();
			}

		};

		final Function<Computer, Maybe<String>> noErrors=NonsensicalArrangement.noErrors();
		return NonsensicalArrangement.customCheck(getAllComputers,warning,noErrors,USUAL).run(network);
	}
}