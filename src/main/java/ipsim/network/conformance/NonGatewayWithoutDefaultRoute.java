package ipsim.network.conformance;

import fj.F;
import fj.data.Option;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.NonsensicalArrangement.customCheck;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getDefaultRoutes;
import static ipsim.util.Collections.size;

class NonGatewayWithoutDefaultRoute extends F<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult f(@NotNull final Network network)
	{
		final F<Computer, Option<String>> isARouter=new F<Computer, Option<String>>()
		{
			@Override
            @NotNull
			public Option<String> f(@NotNull final Computer computer)
			{
				return ConformanceTestsUtility.isARouter().f(computer)||!(0==size(getDefaultRoutes(computer.routingTable))) ? Option.<String>none() : Option.some("Non-gateway computer without a default route");
			}
		};

		final F<Computer, Option<String>> noErrors=noErrors();

		return customCheck(getAllComputers,isARouter,noErrors,USUAL).f(network);
	}
}