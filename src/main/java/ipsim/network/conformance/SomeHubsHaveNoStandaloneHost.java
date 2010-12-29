package ipsim.network.conformance;

import fpeas.function.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asCard;
import ipsim.gui.PositionUtility;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.getAllHubs;
import static ipsim.network.conformance.NonsensicalArrangement.customCheck;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.hub.Hub;
import static ipsim.network.ethernet.CableUtility.getOtherEnd;
import ipsim.network.ethernet.OnlyOneEndConnectedException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

class SomeHubsHaveNoStandaloneHost implements Function<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult run(@NotNull final Network network)
	{
		final Function<Hub, Maybe<String>> warning=new Function<Hub, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> run(@NotNull final Hub hub)
			{
				boolean foundOne=false;

				for (final Cable cable: hub.getCables())
					try
					{
						final @Nullable Card card=asCard(getOtherEnd(network,cable,hub));

						if (card!=null)
						{
							final @Nullable PacketSource computer=PositionUtility.getParent(network,card,0);

							if (computer!=null && 1==computer.children().size())
								foundOne=true;
						}
					}
					catch (OnlyOneEndConnectedException exception)
					{
					}

				return foundOne ? MaybeUtility.<String>nothing() : MaybeUtility.just("Hub with no standalone (non-gateway) computer");
			}

		};

		final Function<Hub, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllHubs,warning,noErrors,USUAL).run(network);
	}
}