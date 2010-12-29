package ipsim.network.conformance;

import fpeas.function.Function;
import ipsim.network.Network;
import static ipsim.network.conformance.ConformanceTestsUtility.someHostsHaveThisManyCards;
import org.jetbrains.annotations.NotNull;

final class SomeHostsHaveMoreThanThreeCards implements Function<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult run(@NotNull final Network network)
	{
		final Function<Integer,Boolean> check1=new Function<Integer,Boolean>()
		{
			@Override
            @NotNull
			public Boolean run(@NotNull final Integer value)
			{
				return value>3;
			}
		};

		return someHostsHaveThisManyCards(network,"3 or more",check1);
	}
}