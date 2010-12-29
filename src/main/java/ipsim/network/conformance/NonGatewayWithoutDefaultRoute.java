package ipsim.network.conformance;

import fpeas.function.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.NonsensicalArrangement.customCheck;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.computer.Computer;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getDefaultRoutes;
import static ipsim.util.Collections.size;
import org.jetbrains.annotations.NotNull;

class NonGatewayWithoutDefaultRoute implements Function<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult run(@NotNull final Network network)
	{
		final Function<Computer, Maybe<String>> isARouter=new Function<Computer, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> run(@NotNull final Computer computer)
			{
				return ConformanceTestsUtility.isARouter().run(computer)||!(0==size(getDefaultRoutes(computer.routingTable))) ? MaybeUtility.<String>nothing() : MaybeUtility.just("Non-gateway computer without a default route");
			}
		};

		final Function<Computer, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllComputers,isARouter,noErrors,USUAL).run(network);
	}
}