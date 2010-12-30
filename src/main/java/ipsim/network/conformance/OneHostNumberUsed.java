package ipsim.network.conformance;

import fj.F;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ipsim.util.Collections.arrayList;

class OneHostNumberUsed extends F<Network,CheckResult> {
    @Override
    @NotNull
	public CheckResult f(@NotNull final Network network)
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