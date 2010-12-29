package ipsim.network.connectivity.computer;

import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import fpeas.sideeffect.SideEffect;
import fpeas.predicate.PredicateUtility;
import ipsim.Caster;
import static ipsim.lang.Assertion.assertNotNull;
import static ipsim.lang.Assertion.assertTrue;
import ipsim.lang.Stringable;
import ipsim.lang.Stringables;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ethernet.ComputerUtility;
import static ipsim.network.ethernet.RouteUtility.isDefaultRoute;
import ipsim.util.Collections;
import static ipsim.util.Collections.arrayList;

import java.util.Collection;
import static java.util.Collections.sort;
import java.util.Comparator;
import java.util.List;

/* TODO make sure that the routing table view is updated after editing without closing and reopening */
public final class RoutingTable implements Stringable
{
	private List<Route> routes=Collections.arrayList();

	public void add(final Maybe<Computer> maybeComputer, final Route route, final SideEffect<IPAddress> ifUnreachableGateway)
	{
		MaybeUtility.run(maybeComputer, new SideEffect<Computer>()
		{
			@Override
            public void run(final Computer computer)
			{
				if (ComputerUtility.isLocallyReachable(computer, route.gateway))
					routes.add(route);
				else
					ifUnreachableGateway.run(route.gateway);
			}
		}, new Runnable()
		{
			@Override
            public void run()
			{
				routes.add(route);
			}
		});
	}

	public void remove(final Route route)
	{
		assertNotNull(route);

		assertTrue(routes.contains(route));
		routes=Collections.only(Collections.<Route>arrayListRef(),routes, PredicateUtility.not(Caster.equalT(route)));
	}

	@Override
    public String asString()
	{
		return Collections.join(Collections.map(routes, Stringables.<Route>asString()), "\n");
	}

	public Collection<Route> routes()
	{
		final List<Route> result=arrayList();

		for (final Route route: routes)
			result.add(route);

		sort(result, new Comparator<Route>()
		{

			@Override
            public int compare(final Route r1, final Route r2)
			{
				if (isDefaultRoute(r1)==isDefaultRoute(r2))
					return 0;

				if (isDefaultRoute(r2))
					return -1;

				return 1;
			}
		});
		return result;
	}

	public void replace(final Route previous, final Route newRoute)
	{
		routes.set(routes.indexOf(previous),newRoute);
	}
}