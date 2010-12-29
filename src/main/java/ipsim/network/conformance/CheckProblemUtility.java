package ipsim.network.conformance;

import fpeas.function.Function;
import ipsim.network.Network;
import ipsim.network.Problem;
import org.jetbrains.annotations.Nullable;

public class CheckProblemUtility
{
	public static CheckResult check(final Network network,final Function<Problem,CheckResult> func)
	{
		@Nullable
		final Problem problem=network.problem;

		return problem==null ? CheckResultUtility.fine() : func.run(problem);
	}
}