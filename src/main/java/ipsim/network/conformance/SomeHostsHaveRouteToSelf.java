package ipsim.network.conformance;

import fj.F;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.NonsensicalArrangement.customCheck;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import static ipsim.network.ethernet.ComputerUtility.getIPAddresses;

class SomeHostsHaveRouteToSelf extends F<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult f(@NotNull final Network context)
	{
		final F<Computer, Maybe<String>> warning=new F<Computer, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> f(@NotNull final Computer computer)
			{
				for (final Route route: computer.routingTable.routes())
					if (getIPAddresses(computer).contains(route.gateway))
						return MaybeUtility.just("Computer with a route that points at itself");

				return MaybeUtility.nothing();
			}
		};

		final F<Computer, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllComputers,warning,noErrors,USUAL).f(context);
	}
}