package ipsim.network.conformance;

import fj.F;
import fj.data.Option;
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
		final F<Computer, Option<String>> warning=new F<Computer, Option<String>>()
		{
			@Override
            @NotNull
			public Option<String> f(@NotNull final Computer computer)
			{
				final Collection<IPAddress> subnets=Collections.hashSet();

				for (final Route route: computer.routingTable.routes())
				{
					if (subnets.contains(route.block.networkNumber) && !equalT(route.block.networkNumber,new IPAddress(0)))
						return Option.some("Computer with more than one route to the same subnet");

					subnets.add(route.block.networkNumber);
				}

				return Option.none();
			}
		};

		final F<Computer, Option<String>> noErrors=noErrors();

		return NonsensicalArrangement.customCheck(getAllComputers,warning,noErrors,USUAL).f(network);
	}
}