package ipsim.network.conformance;

import fj.F;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.Problem;
import ipsim.network.connectivity.PacketSource;
import ipsim.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.conformance.TypicalScores.NONE;
import static ipsim.network.conformance.TypicalScores.USUAL;
import static ipsim.util.Collections.asList;

class PercentOfRequiredSubnets extends F<Network,CheckResult>
{
	private final List<PacketSource> empty=Collections.arrayList();

	@Override
    @NotNull
	public CheckResult f(@NotNull final Network network)
	{
		final F<Problem,CheckResult> func=new F<Problem,CheckResult>()
		{
			@Override
            @NotNull
			public CheckResult f(@NotNull final Problem problem)
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