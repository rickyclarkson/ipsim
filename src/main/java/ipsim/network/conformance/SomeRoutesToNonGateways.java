package ipsim.network.conformance;

import fj.F;
import fj.data.Option;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.NetworkUtility.getComputersByIP;
import static ipsim.network.conformance.NonsensicalArrangement.customCheck;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import static ipsim.util.Collections.any;

class SomeRoutesToNonGateways extends F<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult f(@NotNull final Network network)
	{
		final F<Computer, Option<String>> warning=new F<Computer, Option<String>>()
		{
			@Override
            @NotNull
			public Option<String> f(@NotNull final Computer computer)
			{
				for (final Route route: computer.routingTable.routes())
				{
					final Collection<Computer> gateways=getComputersByIP(network, route.gateway);

					if (gateways.isEmpty())
						return Option.some("Computer with a route to a non-existent gateway");

					if (!any(gateways,new F<Computer, Boolean>()
					{
						@Override
                        public Boolean f(final Computer aComputer)
						{
							return ConformanceTestsUtility.isARouter().f(aComputer);
						}
					}))
						return Option.some("Computer with a route to a computer that is not a gateway");
				}

				return Option.none();
			}

		};

		final F<Computer, Option<String>> noErrors=noErrors();

		return customCheck(getAllComputers,warning,noErrors,USUAL).f(network);
	}
}