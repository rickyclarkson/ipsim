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
import static ipsim.network.connectivity.computer.RoutingTableUtility.getExplicitRoutes;
import ipsim.network.ethernet.NetBlock;
import static ipsim.util.Collections.arrayList;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

class SomeHostsHaveMoreThanOneRouteToTheSamePlace implements Function<Network,CheckResult>
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
				final Collection<NetBlock> blocks=arrayList();

				for (final Route route: getExplicitRoutes(computer.routingTable))
				{
					final NetBlock block=route.block;

					if (blocks.contains(block))
						return MaybeUtility.just("Computer with more than one route to the same network");

					blocks.add(block);
				}

				return MaybeUtility.nothing();
			}
		};

		final Function<Computer, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllComputers,warning,noErrors,USUAL).run(network);
	}
}