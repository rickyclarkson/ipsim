package ipsim.network.conformance;

import fpeas.function.Function;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.util.Collections;
import static ipsim.util.Collections.arrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class OneHostNumberUsed implements Function<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult run(@NotNull final Network network)
	{
		final List<PacketSource> empty=arrayList();
		final List<PacketSource> warnings=arrayList();

		for (final Card card: NetworkUtility.getAllCards(network))
		{
			@Nullable
			final CardDrivers cardWithDrivers=card.withDrivers;

			if (cardWithDrivers==null)
				continue;

			final IPAddress ipAddress=cardWithDrivers.ipAddress.get();
			final NetMask netMask=cardWithDrivers.netMask.get();

			final int x=ipAddress.rawValue&~netMask.rawValue;
			final int y=~netMask.rawValue;
			if (x==y)
				warnings.add(card);
		}

		if (warnings.isEmpty())
			return CheckResultUtility.fine();

		return new CheckResult(TypicalScores.USUAL, Collections.asList("All-1s host number"), warnings, empty);
	}
}