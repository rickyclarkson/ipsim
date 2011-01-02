package ipsim.connectivity;

import com.rickyclarkson.testsuite.UnitTest;
import fj.Effect;
import ipsim.network.Network;
import ipsim.network.connectivity.ConnectivityResults;
import java.io.File;

import static ipsim.network.NetworkUtility.loadFromFile;
import static ipsim.network.connectivity.ConnectivityTest.testConnectivity;

public class UnconnectedFilesTest implements UnitTest
{
	@Override
    public boolean invoke()
        {
		final File directory=new File("datafiles/unconnected");

		for (final File file: directory.listFiles())
		{
			final Network network=new Network();

			loadFromFile(network,file);

			final ConnectivityResults results=testConnectivity(network, Effect.<String>doNothing(), Effect.<Integer>doNothing());
			final boolean isOneHundred=100==results.getPercentConnected();

			if (isOneHundred)
				throw new RuntimeException(file.toString());
		}

		return true;
        }

	public String toString()
        {
	        return "UnconnectedFilesTest";
        }
}