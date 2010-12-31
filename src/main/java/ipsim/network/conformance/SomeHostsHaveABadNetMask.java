package ipsim.network.conformance;

import fj.F;
import fj.data.Option;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.ethernet.NetMaskUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ipsim.network.conformance.NonsensicalArrangement.customCheck;
import static ipsim.network.conformance.TypicalScores.USUAL;

class SomeHostsHaveABadNetMask extends F<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult f(@NotNull final Network network)
	{
		final F<Card, Option<String>> warning=new F<Card, Option<String>>()
		{
			@Override
            @NotNull
			public Option<String> f(@NotNull final Card card)
			{
				@Nullable
				final CardDrivers drivers=card.withDrivers;
				return drivers==null || NetMaskUtility.isValid(drivers.netMask.get()) ? Option.<String>none() : Option.some("Card with a non-standard netmask");
			}
		};

		final F<Card, Option<String>> noErrors=NonsensicalArrangement.noErrors();

		return customCheck(NetworkUtility.getAllCards,warning,noErrors,USUAL).f(network);
	}
}