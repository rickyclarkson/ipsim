package ipsim.network.conformance;

import fpeas.function.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.computer.Computer;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getDefaultRoutes;
import static ipsim.util.Collections.size;
import org.jetbrains.annotations.NotNull;

class SomeHostsHaveMoreThanOneDefaultRoute implements Function<Network,CheckResult>
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
				return size(getDefaultRoutes(computer.routingTable))>1 ? MaybeUtility.just("Computer with more than one default route") : MaybeUtility.<String>nothing();
			}
		};

		final Function<Computer, Maybe<String>> noErrors=noErrors();

		return NonsensicalArrangement.customCheck(getAllComputers,warning,noErrors,USUAL).run(network);
	}
}