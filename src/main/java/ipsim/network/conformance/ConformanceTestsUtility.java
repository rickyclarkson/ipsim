package ipsim.network.conformance;

import fpeas.function.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.NonsensicalArrangement.cableWithEndsDisconnected;
import static ipsim.network.conformance.NonsensicalArrangement.cardWithZeroIP;
import static ipsim.network.conformance.NonsensicalArrangement.cardWithoutCable;
import static ipsim.network.conformance.NonsensicalArrangement.cardWithoutDeviceDrivers;
import static ipsim.network.conformance.NonsensicalArrangement.computerWithoutNetworkCard;
import static ipsim.network.conformance.NonsensicalArrangement.customCheck;
import static ipsim.network.conformance.NonsensicalArrangement.hubWithNoCables;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.RoutePointsAtNonLocalGateway.routePointsAtNonLocalGateway;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.ethernet.ComputerUtility;
import static ipsim.util.Collections.arrayList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ConformanceTestsUtility
{
	public static Collection<Function<Network, CheckResult>> createNetworkCheck()
	{
		final Collection<Function<Network, CheckResult>> checks=arrayList();

		checks.add(new PercentOfIPsMatchingProblem());
		checks.add(new CycleInDefaultRoutes());
		checks.add(new ZeroSubnetMaskUsed());
		checks.add(new OneSubnetMaskUsed());
		checks.add(new PercentOfRequiredSubnets());

		checks.add(computerWithoutNetworkCard());
		checks.add(cardWithoutCable());
		checks.add(cableWithEndsDisconnected());
		checks.add(cardWithoutDeviceDrivers());
		checks.add(cardWithZeroIP());
		checks.add(hubWithNoCables());

		checks.add(new PercentUniqueIPAddresses());
		checks.add(new HubWithMoreThanOneSubnet());
		checks.add(new SomeHostsHaveMoreThanThreeCards());

		checks.add(new ZeroHostNumberUsed());

		checks.add(new OneHostNumberUsed());

		checks.add(new SomeHubsHaveNoStandaloneHost());

		checks.add(new SomeHostsHaveABadNetMask());

		checks.add(new SomeHostsHaveTwoCardsWithTheSameNetworkNumber());

		checks.add(new SomeHostsHaveRouteToSelf());

		checks.add(new SomeHostsHaveMoreThanOneDefaultRoute());

		checks.add(new ExplicitRouteOnNonGateway());

		checks.add(new SomeRoutesToNonGateways());

		checks.add(new ExplicitRouteToLocalNetwork());

		checks.add(new NonGatewayWithoutDefaultRoute());

		checks.add(new PacketForwardingEnabledOnNonGateway());

		checks.add(new SomeHostsHaveMoreThanOneRouteToTheSamePlace());
		checks.add(new MultipleRoutesToTheSameSubnet());
		checks.add(routePointsAtNonLocalGateway);
		
		return checks;
	}

	public static CheckResult someHostsHaveThisManyCards(final Network network, final String string, final Function<Integer, Boolean> thisMany)
	{
		final Function<Computer, Maybe<String>> haveThisMany=new Function<Computer, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> run(@NotNull final Computer computer)
			{
				return thisMany.run(computer.getCards().size()) ? MaybeUtility.just("A computer with "+string+" cards") : MaybeUtility.<String>nothing();
			}
		};

		final Function<Computer, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllComputers, haveThisMany, noErrors, USUAL).run(network);
	}

	public static Function<Computer, Boolean> isARouter()
	{
		return new Function<Computer, Boolean>()
		{
			@Override
            @NotNull
			public Boolean run(@NotNull final Computer computer)
			{
				return ComputerUtility.getIPAddresses(computer).size()>1;
			}
		};
	}

}