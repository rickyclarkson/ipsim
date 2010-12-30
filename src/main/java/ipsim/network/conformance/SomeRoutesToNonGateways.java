package ipsim.network.conformance;

import fj.F;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import fpeas.predicate.Predicate;
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
		final F<Computer, Maybe<String>> warning=new F<Computer, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> f(@NotNull final Computer computer)
			{
				for (final Route route: computer.routingTable.routes())
				{
					final Collection<Computer> gateways=getComputersByIP(network, route.gateway);

					if (gateways.isEmpty())
						return MaybeUtility.just("Computer with a route to a non-existent gateway");

					if (!any(gateways,new Predicate<Computer>()
					{
						@Override
                        public boolean invoke(final Computer aComputer)
						{
							return ConformanceTestsUtility.isARouter().f(aComputer);
						}
					}))
						return MaybeUtility.just("Computer with a route to a computer that is not a gateway");
				}

				return MaybeUtility.nothing();
			}

		};

		final F<Computer, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllComputers,warning,noErrors,USUAL).f(network);
	}
}