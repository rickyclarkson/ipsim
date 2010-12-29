package ipsim.network.conformance;

import fpeas.function.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import static ipsim.Caster.equalT;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.util.Collections;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class MultipleRoutesToTheSameSubnet implements Function<Network,CheckResult>
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

		final Function<Computer, Maybe<String>> noErrors=noErrors();

		return NonsensicalArrangement.customCheck(getAllComputers,warning,noErrors,USUAL).run(network);
	}
}