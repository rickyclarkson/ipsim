package ipsim.network.conformance;

import fj.F;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.util.Collections;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

import static ipsim.Caster.equalT;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;

public class MultipleRoutesToTheSameSubnet extends F<Network,CheckResult>
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
				final Collection<IPAddress> subnets=Collections.hashSet();

				for (final Route route: computer.routingTable.routes())
				{
					if (subnets.contains(route.block.networkNumber) && !equalT(route.block.networkNumber,new IPAddress(0)))
						return MaybeUtility.just("Computer with more than one route to the same subnet");

					subnets.add(route.block.networkNumber);
				}

				return MaybeUtility.nothing();
			}
		};

		final F<Computer, Maybe<String>> noErrors=noErrors();

		return NonsensicalArrangement.customCheck(getAllComputers,warning,noErrors,USUAL).f(network);
	}
}