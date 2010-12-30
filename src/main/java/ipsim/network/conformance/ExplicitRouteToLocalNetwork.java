package ipsim.network.conformance;

import fj.F;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import fpeas.predicate.Predicate;
import ipsim.network.Network;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.NoDeviceDriversException;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.util.Collections;
import org.jetbrains.annotations.NotNull;

import static ipsim.Caster.equalT;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getExplicitRoutes;
import static ipsim.network.ethernet.CardUtility.getNetBlock;

class ExplicitRouteToLocalNetwork extends F<Network,CheckResult>
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
				return Collections.any(computer.getCards(),new Predicate<Card>()
				{
					@Override
                    public boolean invoke(final Card card)
					{
						for (final Route route: getExplicitRoutes(computer.routingTable))
							try
							{
								if (equalT(route.block,getNetBlock(card)))
									return true;
							}
							catch (NoDeviceDriversException exception)
							{
							}

						return false;
					}

				}) ? MaybeUtility.just("Computer with an explicit route that points to one of its local networks") : MaybeUtility.<String>nothing();
			}
		};

		final F<Computer, Maybe<String>> noErrors=noErrors();

		return NonsensicalArrangement.customCheck(getAllComputers,warning,noErrors,USUAL).f(network);
	}
}