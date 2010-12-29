package ipsim.network.conformance;

import fpeas.function.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import static ipsim.network.ethernet.ComputerUtility.isLocallyReachable;
import org.jetbrains.annotations.NotNull;

public class RoutePointsAtNonLocalGateway
{
	public static final Function<Network, CheckResult> routePointsAtNonLocalGateway=new Function<Network, CheckResult>()
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
					for (final Route route : computer.routingTable.routes())
						if (!isLocallyReachable(computer, route.gateway))
							return MaybeUtility.just("Route with a non-local gateway (this is a bug if it isn't from an old saved file)");

					return MaybeUtility.nothing();
				}
			};

			final Function<Computer, Maybe<String>> noErrors=noErrors();

			return NonsensicalArrangement.customCheck(getAllComputers, warning, noErrors, USUAL).run(network);
		}
	};
}