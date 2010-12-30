package ipsim.network.conformance;

import fj.F;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.ethernet.NetBlock;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.NonsensicalArrangement.customCheck;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getExplicitRoutes;
import static ipsim.util.Collections.arrayList;

class SomeHostsHaveMoreThanOneRouteToTheSamePlace extends F<Network,CheckResult>
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

		final F<Computer, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllComputers,warning,noErrors,USUAL).f(network);
	}
}