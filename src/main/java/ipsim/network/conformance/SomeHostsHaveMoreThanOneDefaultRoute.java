package ipsim.network.conformance;

import fj.F;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getDefaultRoutes;
import static ipsim.util.Collections.size;

class SomeHostsHaveMoreThanOneDefaultRoute extends F<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult f(@NotNull final Network network)
	{
		final F<Computer, Maybe<String>> warning=new F<Computer, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> f(@NotNull final Computer computer)
			{
				return size(getDefaultRoutes(computer.routingTable))>1 ? MaybeUtility.just("Computer with more than one default route") : MaybeUtility.<String>nothing();
			}
		};

		final F<Computer, Maybe<String>> noErrors=noErrors();

		return NonsensicalArrangement.customCheck(getAllComputers,warning,noErrors,USUAL).f(network);
	}
}