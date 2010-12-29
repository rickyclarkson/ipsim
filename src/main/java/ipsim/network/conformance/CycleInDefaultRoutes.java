package ipsim.network.conformance;

import fpeas.function.Function;
import fpeas.function.FunctionUtility;
import fpeas.predicate.Predicate;
import static ipsim.Caster.equalT;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.NetworkUtility.getComputersByIP;
import static ipsim.network.conformance.CheckResultUtility.fine;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getDefaultRoutes;
import static ipsim.network.ethernet.RouteUtility.isDefaultRoute;
import static ipsim.network.ethernet.RouteUtility.isRouteToSelf;
import ipsim.util.Collections;
import static ipsim.util.Collections.any;
import static ipsim.util.Collections.arrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class CycleInDefaultRoutes implements Function<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult run(@NotNull final Network network)
	{
		final List<PacketSource> empty=arrayList();

		for (final Computer computer: getAllComputers(network))
			for (final Route route: computer.routingTable.routes())
				if (isDefaultRoute(route)&&!getComputersByIP(network, route.gateway).contains(computer))
					if (detectCycle(network, FunctionUtility.<Computer,Boolean>constant(false),computer,route))
						return new CheckResult(USUAL, Collections.asList("Cycle in default routes"), empty, empty);

		return fine();
	}

	public static boolean detectCycle(final Network network,final Function<Computer,Boolean> containsComputer,final Computer computer,final Route route)
	{
		if (isRouteToSelf(computer,route))
			return false;

		return any(getComputersByIP(network, route.gateway),new Predicate<Computer>()
		{
			@Override
            public boolean invoke(final Computer computer1)
			{
				if (containsComputer.run(computer1))
					return true;

				return any(getDefaultRoutes(computer1.routingTable),new Predicate<Route>()
				{
					@Override
                    public boolean invoke(final Route route2)
					{
						return detectCycle(network,new Function<Computer,Boolean>()
						{
							@Override
                            @NotNull
							public Boolean run(@NotNull final Computer aComputer)
							{
								return equalT(aComputer,computer1)||containsComputer.run(aComputer);
							}
						},computer,route2);
					}

				});
			}
		});
	}
}