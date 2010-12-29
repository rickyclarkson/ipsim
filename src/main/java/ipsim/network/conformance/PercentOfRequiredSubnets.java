package ipsim.network.conformance;

import fpeas.function.Function;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.Problem;
import static ipsim.network.conformance.TypicalScores.NONE;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.PacketSource;
import ipsim.util.Collections;
import static ipsim.util.Collections.asList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class PercentOfRequiredSubnets implements Function<Network,CheckResult>
{
	private final List<PacketSource> empty=Collections.arrayList();

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
				final int ideal=problem.numberOfSubnets;
				final int actual=NetworkUtility.getNumberOfSubnets(network);

				if (actual>ideal)
					return new CheckResult(USUAL, asList("More subnets in the solution than are in the problem"), empty, empty);

				final int percent;
				if (actual==ideal)
					percent=100;
				else
					percent=100*(ideal-(ideal-actual))/ideal;

				if (!(100==percent))
					return new CheckResult(percent, asList("Less subnets in the solution than the problem requires"), empty, empty);

				return new CheckResult(NONE, asList("The correct number of subnets for the problem"), empty, empty);
			}
		};

		return CheckProblemUtility.check(network,func);
	}
}