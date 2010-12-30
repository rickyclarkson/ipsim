package ipsim.connectivity;

import com.rickyclarkson.testsuite.UnitTest;
import fj.F;
import fpeas.sideeffect.SideEffectUtility;
import ipsim.Globals;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ping.PingResults;
import ipsim.network.connectivity.ping.Pinger;
import ipsim.network.ip.CheckedNumberFormatException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.NetworkUtility.getComputersByIP;
import static ipsim.network.ip.IPAddressUtility.valueOf;
import static ipsim.util.Collections.all;

public class RoutingLoopTest implements UnitTest
{
	@Override
    public boolean invoke()
	{
		final Network context=new Network();

		NetworkUtility.loadFromFile(context,new File("datafiles/fullyconnected/routingloop1.ipsim"),SideEffectUtility.<IOException>throwRuntimeException());

		try
		{
			final IPAddress ipAddress=valueOf("146.87.1.1");
			final IPAddress ipAddress2=valueOf("146.87.2.1");

			return all(getComputersByIP(context,ipAddress),new F<Computer,Boolean>()
			{
				@Override
                @NotNull
				public Boolean f(@NotNull final Computer computer)
				{
					final List<PingResults> results=Pinger.ping(context,computer, new DestIPAddress(ipAddress2), Globals.DEFAULT_TIME_TO_LIVE);

					return 1==results.size() &&results.iterator().next().ttlExpired();
				}
			});
		}
		catch (final CheckedNumberFormatException exception)
		{
			throw null;
		}
	}

	public String toString()
	{
		return "RoutingLoopTest";
	}
}
