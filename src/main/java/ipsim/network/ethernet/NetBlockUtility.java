package ipsim.network.ethernet;

import fj.data.Either;
import ipsim.network.InvalidNetMaskException;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;

import static fpeas.either.EitherUtility.left;
import static fpeas.either.EitherUtility.right;
import static fpeas.either.EitherUtility.unsafeLeft;

public final class NetBlockUtility
{
	public static NetBlock getZero()
	{
		return new NetBlock(IPAddressUtility.zero, NetMaskUtility.zero);
	}

	public static String asStringContainingSlash(final NetBlock netBlock) throws InvalidNetMaskException
	{
		return netBlock.networkNumber.asString()+'/'+NetMaskUtility.getPrefixLength(netBlock.netMask);
	}

	public static NetBlock createNetBlockOrThrowRuntimeException(final String net)
	{
		return createNetBlock(net).left().value();
	}

	public static final class ParseFailure
	{
		private final String reason;

		public ParseFailure(final String reason)
		{
			this.reason=reason;
		}

		@Override
		public String toString()
		{
			return reason;
		}
	}

	public static Either<NetBlock,ParseFailure> createNetBlock(final String string)
	{
		final String regexp="([0-9]{1,3}\\.){3}[0-9]{1,3}/[0-9]{1,2}";

		if (!string.matches(regexp))
			return Either.right(new ParseFailure("Regex match failed on "+string));

		final String[] parts=string.split("/");

		final IPAddress networkNumber;
		try
		{
			networkNumber=IPAddressUtility.valueOf(parts[0]);
		}
		catch (final CheckedNumberFormatException exception)
		{
			return Either.right(new ParseFailure("IPAddressUtility.valueOf threw an exception on "+parts[0]));
		}

		final int tempMask=Integer.parseInt(parts[1]);

		if (tempMask>32)
			return Either.right(new ParseFailure("Netmask bigger than 32"));

		final NetMask netMask=NetMaskUtility.createNetMaskFromPrefixLength(tempMask);

		final int y=networkNumber.rawValue&~netMask.rawValue;
		if (!(0==y))
			return Either.right(new ParseFailure("Host part not blank in "+string));

		return Either.left(new NetBlock(networkNumber, netMask));
	}

	public static IPAddress getBroadcastAddress(final NetBlock block)
	{
		final int rawValue=~block.netMask.rawValue|block.networkNumber.rawValue;
		return new IPAddress(rawValue);
	}

	public static String asCustomString(final NetBlock block)
	{
		try
		{
			return IPAddressUtility.toString(block.networkNumber.rawValue)+'/'+NetMaskUtility.getPrefixLength(block.netMask);
		}
		catch (final InvalidNetMaskException exception)
		{
			return IPAddressUtility.toString(block.networkNumber.rawValue)+'/'+NetMask.asString(block.netMask.rawValue);
		}
	}
}