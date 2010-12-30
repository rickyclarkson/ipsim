package ipsim.network.conformance;

import fj.F;
import ipsim.network.Network;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.conformance.ConformanceTestsUtility.someHostsHaveThisManyCards;

final class SomeHostsHaveMoreThanThreeCards extends F<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult f(@NotNull final Network network)
	{
		final F<Integer,Boolean> check1=new F<Integer,Boolean>()
		{
			@Override
            @NotNull
			public Boolean f(@NotNull final Integer value)
			{
				return value>3;
			}
		};

		return someHostsHaveThisManyCards(network,"3 or more",check1);
	}
}