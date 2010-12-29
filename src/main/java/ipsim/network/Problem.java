package ipsim.network;

import fpeas.maybe.Maybe;
import static ipsim.network.ProblemUtility.createProblem;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.IPAddressUtility;

public class Problem
{
	public final NetBlock netBlock;

	public final int numberOfSubnets;

	public Problem(final NetBlock startingNetBlock, final int startingNumberOfSubnets)
	{
		netBlock=startingNetBlock;
		numberOfSubnets=startingNumberOfSubnets;
	}

	public String asString()
	{
		try
		{
			return "Network number: "+IPAddressUtility.toString(netBlock.networkNumber.rawValue)+'/'+NetMaskUtility.getPrefixLength(netBlock.netMask)+" Number of subnets: "+numberOfSubnets;
		}
		catch (final InvalidNetMaskException exception)
		{
			throw new RuntimeException(exception);
		}
	}

	public Maybe<Problem> withNetBlock(final NetBlock newNetBlock)
	{
		return createProblem(newNetBlock, numberOfSubnets);
	}

	public Maybe<Problem> withSubnets(final int newSubnets)
	{
		return createProblem(netBlock, newSubnets);
	}

	public static final int MIN_SUBNETS=2, MAX_SUBNETS=32;
}