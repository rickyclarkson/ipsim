package ipsim.tests;

import com.rickyclarkson.testsuite.UnitTest;
import fpeas.function.Function;
import ipsim.Caster;
import static ipsim.connectivity.PingTester.testPing;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ping.PingResults;
import ipsim.network.ip.CheckedNumberFormatException;
import static ipsim.network.ip.IPAddressUtility.valueOf;
import static ipsim.util.Collections.all;

import java.io.File;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class RemoteBroadcastBug implements UnitTest
{
	@Override
    public boolean invoke()
	{
		final Network network=new Network();

		NetworkUtility.loadFromFile(network,new File("datafiles/fullyconnected/101.ipsim"));

		final IPAddress ip41;
		try
		{
			ip41=valueOf("146.87.4.1");
		}
		catch (final CheckedNumberFormatException exception)
		{
			return false;
		}

		try
		{
			return all(testPing(network,valueOf("146.87.1.1"),valueOf("146.87.4.255")),new Function<List<PingResults>,Boolean>()
			{
				@Override
                @NotNull
				public Boolean run(@NotNull final List<PingResults> results)
				{
					final PingResults result=results.iterator().next();

					return result.pingReplyReceived() && Caster.equalT(result.getReplyingHost().getIPAddress(),ip41) && 1==results.size();
				}

			});
		}
		catch (final CheckedNumberFormatException exception)
		{
			return false;
		}
	}

	public String toString()
	{
		return "RemoteBroadcastBug";
	}
}