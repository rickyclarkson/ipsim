package ipsim.network.ip;

import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.SourceIPAddress;

public class IPAddressUtility
{
	public static final IPAddress zero=new IPAddress(0);

	public static IPAddress valueOf(final String ip) throws CheckedNumberFormatException
	{
		if (!ip.matches("([0-9]{1,3}\\.){3}[0-9]{1,3}"))
			throw new CheckedNumberFormatException('\''+ip+"' is not a valid IP address");

		final String[] parts=ip.split("\\.");

		final int[] p={Integer.parseInt(parts[0]),Integer.parseInt(parts[1]),Integer.parseInt(parts[2]),Integer.parseInt(parts[3])};

		if (p[0]>255||p[1]>255||p[2]>255||p[3]>255)
			throw new CheckedNumberFormatException('\''+ip+"' is not a valid IP address");

		return new IPAddress(p[0]<<24|p[1]<<16|p[2]<<8|p[3]);
	}

	public static String toString(final int rawValue)
	{
		return (rawValue>>>24)+"."+(rawValue>>16&0xff)+'.'+(rawValue>>8&0xff)+'.'+(rawValue&0xff);
	}

	public static DestIPAddress sourceToDest(final SourceIPAddress sourceIPAddress)
	{
		return new DestIPAddress(sourceIPAddress.getIPAddress());
	}

	public static SourceIPAddress destToSource(final DestIPAddress destIPAddress)
	{
		return new SourceIPAddress(destIPAddress.getIPAddress());
	}

	public static IPAddress randomIP()
	{
		return new IPAddress((int)((long)(Math.random()*Integer.MAX_VALUE*2)&0xFFFFFFFFL));
	}

	public static IPAddress valueOfUnchecked(final String s)
	{
		try
		{
			return valueOf(s);
		}
		catch (CheckedNumberFormatException e)
		{
			throw new RuntimeException(e);
		}
	}
}