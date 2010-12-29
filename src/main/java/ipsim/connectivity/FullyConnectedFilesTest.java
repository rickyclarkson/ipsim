package ipsim.connectivity;

import com.rickyclarkson.testsuite.UnitTest;
import fpeas.sideeffect.SideEffect;
import static fpeas.sideeffect.SideEffectUtility.doNothing;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.ConnectivityResults;
import ipsim.network.connectivity.ConnectivityTest;

import java.io.File;

public class FullyConnectedFilesTest implements UnitTest
{
	@Override
    public boolean invoke()
	{
		final File directory=new File("datafiles/fullyconnected");

		for (final File file: directory.listFiles())
		{
			final Network network=new Network();

			NetworkUtility.loadFromFile(network,file);

			final SideEffect<String> log=doNothing();
			final SideEffect<Integer> progress=doNothing();
			final ConnectivityResults results=ConnectivityTest.testConnectivity(network, log, progress);

			if (!(100==results.getPercentConnected()))
			{
				throw new RuntimeException(file.toString()+": "+results.asString());
			}
		}

		return true;
	}

	public String toString()
	{
		return "FullyConnectedFilesTest";
	}
}