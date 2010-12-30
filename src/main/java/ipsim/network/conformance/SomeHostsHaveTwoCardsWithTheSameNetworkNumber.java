/**
 *
 */
package ipsim.network.conformance;

import fj.F;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ethernet.CardUtility;
import ipsim.network.ethernet.ComputerUtility;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.TypicalScores.USUAL;
import static ipsim.util.Collections.hashSet;

class SomeHostsHaveTwoCardsWithTheSameNetworkNumber extends F<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult f(@NotNull final Network network)
	{
		return NonsensicalArrangement.customCheck(getAllComputers,new F<Computer, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> f(@NotNull final Computer computer)
			{
				final Collection<IPAddress> netNumbers=hashSet();

				for (final CardDrivers card: ComputerUtility.cardsWithDrivers(computer))
				{
					final IPAddress netNum=CardUtility.getNetBlock(card).networkNumber;

					if (netNumbers.contains(netNum))
						return MaybeUtility.just("Computer that has multiple cards with the same subnet number");

					netNumbers.add(netNum);
				}

				return MaybeUtility.nothing();
			}
		},NonsensicalArrangement.<Computer>noErrors(),USUAL).f(network);
	}
}