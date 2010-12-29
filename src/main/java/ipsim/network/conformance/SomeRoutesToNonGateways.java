package ipsim.network.conformance;

import fpeas.function.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import fpeas.predicate.Predicate;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.NetworkUtility.getComputersByIP;
import static ipsim.network.conformance.NonsensicalArrangement.customCheck;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import static ipsim.util.Collections.any;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

class SomeRoutesToNonGateways implements Function<Network,CheckResult>
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
							return ConformanceTestsUtility.isARouter().run(aComputer);
						}
					}))
						return MaybeUtility.just("Computer with a route to a computer that is not a gateway");
				}

				return MaybeUtility.nothing();
			}

		};

		final Function<Computer, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllComputers,warning,noErrors,USUAL).run(network);
	}
}