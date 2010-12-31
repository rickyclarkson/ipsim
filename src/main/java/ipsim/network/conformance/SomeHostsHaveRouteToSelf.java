package ipsim.network.conformance;

import fj.F;
import fj.data.Option;
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
		final F<Computer, Option<String>> warning=new F<Computer, Option<String>>()
		{
			@Override
            @NotNull
			public Option<String> f(@NotNull final Computer computer)
			{
				for (final Route route: computer.routingTable.routes())
					if (getIPAddresses(computer).contains(route.gateway))
						return Option.some("Computer with a route that points at itself");

				return Option.none();
			}
		};

		final F<Computer, Option<String>> noErrors=noErrors();

		return customCheck(getAllComputers,warning,noErrors,USUAL).f(context);
	}
}