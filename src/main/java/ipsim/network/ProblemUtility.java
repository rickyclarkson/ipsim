package ipsim.network;

import fpeas.maybe.Maybe;
import static fpeas.maybe.MaybeUtility.just;
import static fpeas.maybe.MaybeUtility.nothing;
import ipsim.Global;
import static ipsim.network.Problem.MAX_SUBNETS;
import static ipsim.network.Problem.MIN_SUBNETS;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ethernet.NetBlock;
import static ipsim.network.ethernet.NetBlockUtility.createNetBlockOrThrowRuntimeException;
import ipsim.network.ethernet.NetMaskUtility;
import static ipsim.network.ethernet.NetMaskUtility.createNetMaskFromPrefixLength;
import ipsim.util.Collections;

import javax.swing.*;
import java.util.List;
import java.util.Random;

public final class ProblemUtility
{
	public static Maybe<Problem> createProblem(final NetBlock startingNetBlock,final int startingNumberOfSubnets)
	{
		if (startingNumberOfSubnets<MIN_SUBNETS||startingNumberOfSubnets>MAX_SUBNETS)
			return nothing();

		if (isValidNetworkNumber(startingNetBlock.networkNumber))
		{
			if (!NetMaskUtility.isValid(startingNetBlock.netMask))
				return nothing();
		}
		else
			return nothing();

		final Problem problem=new Problem(startingNetBlock, startingNumberOfSubnets);

		return just(problem);
	}

	private static final List<NetBlock> reservedNetBlocks=Collections.asList(createNetBlockOrThrowRuntimeException("10.0.0.0/8"),createNetBlockOrThrowRuntimeException("192.168.0.0/16"),createNetBlockOrThrowRuntimeException("172.16.0.0/12"));

	public static boolean isReservedNetworkNumber(final NetBlock block)
	{
		for (final NetBlock temp: reservedNetBlocks)
		{
			if (block.networkNumber.bitwiseAnd(block.netMask)==temp.networkNumber.bitwiseAnd(block.netMask))
				return true;

			if (block.networkNumber.bitwiseAnd(temp.netMask)==temp.networkNumber.bitwiseAnd(temp.netMask))
				return true;
		}

		return false;
	}

	public static boolean isValidNetworkNumber(final IPAddress networkNumber)
	{
		final int networkNumberBits=networkNumber.rawValue;
		final int firstByte=networkNumberBits>>>24;

		switch (firstByte)
		{
			case 0:
				return false;

			case 127:
				return false;

			default:
				return firstByte<224;
		}
	}

	public static IPAddress generateNetworkNumber(final int prefixLength)
	{
		for (int a=0;a<1000;a++)
		{
			int networkNumber=random.nextInt(Integer.MAX_VALUE);

			networkNumber<<=32-prefixLength; // remove host
			// part

			final IPAddress returnValue=new IPAddress(networkNumber);

			if (isReservedNetworkNumber(new NetBlock(returnValue, createNetMaskFromPrefixLength(prefixLength))))
				return returnValue;
		}

		JOptionPane.showMessageDialog(Global.global.get().frame, "Cannot generate a publically-accessible "+"network number with a prefix length of "+prefixLength, "Error", JOptionPane.ERROR_MESSAGE);

		throw new RuntimeException();
	}

	private static final Random random=new Random();
}