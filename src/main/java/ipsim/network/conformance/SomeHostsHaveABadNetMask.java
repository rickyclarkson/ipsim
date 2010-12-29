package ipsim.network.conformance;

import fpeas.function.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import static ipsim.network.conformance.NonsensicalArrangement.customCheck;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.ethernet.NetMaskUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class SomeHostsHaveABadNetMask implements Function<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult run(@NotNull final Network network)
	{
		final Function<Card, Maybe<String>> warning=new Function<Card, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> run(@NotNull final Card card)
			{
				@Nullable
				final CardDrivers drivers=card.withDrivers;
				return drivers==null || NetMaskUtility.isValid(drivers.netMask.get()) ? MaybeUtility.<String>nothing() : MaybeUtility.just("Card with a non-standard netmask");
			}
		};

		final Function<Card, Maybe<String>> noErrors=NonsensicalArrangement.noErrors();

		return customCheck(NetworkUtility.getAllCards,warning,noErrors,USUAL).run(network);
	}
}