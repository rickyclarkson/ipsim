package ipsim.network.conformance;

import fpeas.function.Function;
import ipsim.network.InvalidNetMaskException;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.Problem;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.card.CardDrivers;
import static ipsim.network.ethernet.NetMaskUtility.getPrefixLength;
import static ipsim.util.Collections.arrayList;
import static ipsim.util.Collections.asList;

import java.util.List;

import org.jetbrains.annotations.NotNull;

class OneSubnetMaskUsed implements Function<Network, CheckResult>
{
	@Override
    @NotNull
	public CheckResult run(@NotNull final Network network)
	{
		final Function<Problem, CheckResult> func=new Function<Problem, CheckResult>()
		{
			@Override
            @NotNull
			public CheckResult run(@NotNull final Problem problem)
			{
				final int rawProblemNumber=problem.netBlock.networkNumber.rawValue;

				final int problemPrefix;
				try
				{
					problemPrefix=getPrefixLength(problem.netBlock.netMask);
				}
				catch (final InvalidNetMaskException exception1)
				{
					throw new RuntimeException(exception1);
				}

				for (final CardDrivers card : NetworkUtility.getAllCardsWithDrivers(network))
				{
					final int rawNetworkNumber=card.ipAddress.get().rawValue&card.netMask.get().rawValue;
					final int cardPrefix;
					try
					{
						cardPrefix=getPrefixLength(card.netMask.get());
					}
					catch (final InvalidNetMaskException exception)
					{
						continue;
					}

					final List<PacketSource> empty=arrayList();
					if (!(cardPrefix==problemPrefix) && rawNetworkNumber-rawProblemNumber==(1<<cardPrefix-problemPrefix)-1)
						return new CheckResult(USUAL, asList("A subnet that uses an all-1s subnet number"), empty, empty);
				}

				return CheckResultUtility.fine();
			}
		};

		return CheckProblemUtility.check(network, func);
	}
}