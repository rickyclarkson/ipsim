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
import ipsim.network.connectivity.computer.Route;
import static ipsim.network.ethernet.ComputerUtility.getIPAddresses;
import org.jetbrains.annotations.NotNull;

class SomeHostsHaveRouteToSelf implements Function<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult run(@NotNull final Network context)
	{
		final Function<Computer, Maybe<String>> warning=new Function<Computer, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> run(@NotNull final Computer computer)
			{
				for (final Route route: computer.routingTable.routes())
					if (getIPAddresses(computer).contains(route.gateway))
						return MaybeUtility.just("Computer with a route that points at itself");

				return MaybeUtility.nothing();
			}
		};

		final Function<Computer, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllComputers,warning,noErrors,USUAL).run(context);
	}
}