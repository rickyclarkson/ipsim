package ipsim.connectivity;

import com.rickyclarkson.testsuite.UnitTest;
import fpeas.function.Function;
import static ipsim.connectivity.PingTester.testPing;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.loadFromFile;
import ipsim.network.connectivity.ping.PingResults;
import ipsim.network.ip.CheckedNumberFormatException;
import static ipsim.network.ip.IPAddressUtility.valueOf;
import ipsim.util.Collections;

import java.io.File;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class BroadcastPingTest implements UnitTest
{
	@Override
    public boolean invoke()
	{
		final Network network=new Network();
		loadFromFile(network,new File("datafiles/fullyconnected/broadcast1.ipsim"));

		try
		{
			return Collections.all(testPing(network,valueOf("146.87.1.1"),valueOf("146.87.1.255")),new Function<List<PingResults>,Boolean>()
			{
				@Override
                @NotNull
				public Boolean run(@NotNull final List<PingResults> results)
				{
					if (!(2==results.size()))
						throw new RuntimeException(String.valueOf(results.size()));

					return 2==results.size();
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
		return "BroadcastPingTest";
	}
}
