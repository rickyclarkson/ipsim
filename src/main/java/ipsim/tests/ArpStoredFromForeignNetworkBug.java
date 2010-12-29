package ipsim.tests;

import com.rickyclarkson.testsuite.UnitTest;
import fpeas.function.Function;
import ipsim.connectivity.PingTester;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import static ipsim.network.NetworkUtility.getComputersByIP;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;
import static ipsim.network.ip.IPAddressUtility.valueOf;
import static ipsim.util.Collections.all;

import java.io.File;

import org.jetbrains.annotations.NotNull;

public class ArpStoredFromForeignNetworkBug implements UnitTest
{
	@Override
    public boolean invoke()
	{
		final Network network=new Network();

		NetworkUtility.loadFromFile(network,new File("datafiles/fullyconnected/arpforeign.ipsim"));

		try
		{
			PingTester.testPing(network,valueOf("146.87.1.1"),valueOf("146.87.2.1"));

			return all(getComputersByIP(network,valueOf("146.87.1.1")),new Function<Computer,Boolean>()
			{
				@Override
                @NotNull
				public Boolean run(@NotNull final Computer computer)
				{
					try
					{
						return computer.arpTable.getMacAddress(IPAddressUtility.valueOf("146.87.2.1"))==null;
					}
					catch (final CheckedNumberFormatException exception)
					{
						throw new RuntimeException(exception);
					}
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
		return "ArpStoredFromForeignNetworkBug";
	}
}