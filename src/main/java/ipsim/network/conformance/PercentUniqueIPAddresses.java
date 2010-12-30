package ipsim.network.conformance;

import fj.F;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.util.Collections;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

import static ipsim.Caster.equalT;
import static ipsim.util.Collections.arrayList;
import static ipsim.util.Collections.count;

class PercentUniqueIPAddresses extends F<Network, CheckResult>
{
	@Override
    @NotNull
	public CheckResult f(@NotNull final Network network)
	{
		final Map<IPAddress, Integer> count=Collections.hashMap();

		int total=0;

		for (final Card card : NetworkUtility.getAllCards(network))
		{
			final CardDrivers cardWithDrivers=card.withDrivers;

			if (cardWithDrivers==null)
				total--;
			else
			{
				if (0==cardWithDrivers.ipAddress.get().rawValue)
					total--;
				else
				{
					if (null!=count.get(cardWithDrivers.ipAddress.get()))
						count.put(cardWithDrivers.ipAddress.get(), count.get(cardWithDrivers.ipAddress.get())+1);
					else
						count.put(cardWithDrivers.ipAddress.get(), 1);
				}
			}

			total++;
		}

		final int percent=equalT(0, total) ? 0 : count(count.values(), equalT(1))*100/total;
		final List<PacketSource> empty=arrayList();

		return new CheckResult(percent, Collections.asList(100-percent+"% of the IP addresses are identical"), empty, empty);
	}
}