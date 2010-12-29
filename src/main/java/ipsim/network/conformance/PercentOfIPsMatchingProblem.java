package ipsim.network.conformance;

import fpeas.function.Function;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.Problem;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.ethernet.NetBlock;
import ipsim.util.Collections;
import static ipsim.util.Collections.arrayList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

class PercentOfIPsMatchingProblem implements Function<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult run(@NotNull final Network network)
	{
		final Function<Problem,CheckResult> func=new Function<Problem,CheckResult>()
		{
			@Override
            @NotNull
			public CheckResult run(@NotNull final Problem problem)
			{
				final NetBlock block=new NetBlock(problem.netBlock.networkNumber, problem.netBlock.netMask);

				final Collection<CardDrivers> cards=NetworkUtility.getAllCardsWithDrivers(network);

				int totalCorrect=0;
				int total=0;

				final List<PacketSource> withWarnings=arrayList();

				for (@NotNull final CardDrivers card: cards)
				{
					if (card.ipAddress.get().rawValue==0)
						continue;

					if (block.networkContains(card.ipAddress.get()))
						totalCorrect++;
					else
						withWarnings.add(card.card);

					total++;
				}

				final int percent=0==total ? 0 : totalCorrect*100/total;

				final List<PacketSource> errors=arrayList();

				return new CheckResult(percent, Collections.asList("IP address that doesn't match the problem given"),withWarnings,errors);
			}
		};

		return CheckProblemUtility.check(network,func);
	}
}