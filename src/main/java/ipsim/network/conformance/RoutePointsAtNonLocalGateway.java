package ipsim.network.conformance;

import fj.F;
import fj.data.Option;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import static ipsim.network.ethernet.ComputerUtility.isLocallyReachable;

public class RoutePointsAtNonLocalGateway
{
	public static final F<Network, CheckResult> routePointsAtNonLocalGateway=new F<Network, CheckResult>()
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
					for (final Route route : computer.routingTable.routes())
						if (!isLocallyReachable(computer, route.gateway))
							return Option.some("Route with a non-local gateway (this is a bug if it isn't from an old saved file)");

					return Option.none();
				}
			};

			final F<Computer, Option<String>> noErrors=noErrors();

			return NonsensicalArrangement.customCheck(getAllComputers, warning, noErrors, USUAL).f(network);
		}
	};
}