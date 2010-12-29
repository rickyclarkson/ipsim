package ipsim.network.conformance;

import static fpeas.maybe.MaybeUtility.nothing;
import fpeas.function.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.getAllCards;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.ip.IPAddress;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ZeroHostNumberUsed implements Function<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult run(@NotNull final Network network)
	{
		return NonsensicalArrangement.customCheck(getAllCards,new Function<Card, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> run(@NotNull final Card card)
			{
				@Nullable
				final CardDrivers cardWithDrivers=card.withDrivers;

				if (cardWithDrivers==null)
					return nothing();

				final IPAddress address=cardWithDrivers.ipAddress.get();
				if (0==address.rawValue)
					return nothing();

				if (0==(address.rawValue&~cardWithDrivers.netMask.get().rawValue))
					return MaybeUtility.just("Card with an all-0s host part of its IP address");

				return nothing();
			}
		},NonsensicalArrangement.<Card>noErrors(),USUAL).run(network);
	}
}