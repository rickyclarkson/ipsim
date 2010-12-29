package ipsim.network.conformance;

import static ipsim.network.conformance.ConformanceTestsUtility.createNetworkCheck;
import fpeas.function.Function;
import ipsim.network.Network;
import ipsim.util.Collections;

import java.util.Collection;
import java.util.List;

public class ConformanceTests
{
	public static ResultsAndSummaryAndPercent allChecks(final Network network)
	{
		final Collection<Function<Network, CheckResult>> checks=createNetworkCheck();
		final StringBuilder answer=new StringBuilder();

		double totalPercent=100;

		final List<CheckResult> checkResults=Collections.arrayList();

		for (final Function<Network,CheckResult> check: checks)
		{
			final CheckResult result=check.run(network);

			final int percent=result.deductions;

			totalPercent*=percent;
			totalPercent/=100;

			final List<String> summary=result.summary;

			if (!(100==percent))
			{
				answer.append(summary.isEmpty() ? "" : summary.get(0));

				checkResults.add(result);
				answer.append(".<p>");
			}
		}

		return new ResultsAndSummaryAndPercent(checkResults,answer.toString(),(int)Math.round(totalPercent));
	}

	public static final class ResultsAndSummaryAndPercent
	{
		public final List<CheckResult> results;
		public final String summary;
		public final int percent;

		public ResultsAndSummaryAndPercent(final List<CheckResult> results,final String summary,final int percent)
		{
			this.results=results;
			this.summary=summary;
			this.percent=percent;
		}
	}
}