package ipsim.tests;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;

import java.io.File;

public class XMLSerialisationTest implements UnitTest
{
	@Override
    public boolean invoke()
	{
		final Network context=new Network();
		NetworkUtility.loadFromFile(context,new File("datafiles/unconnected/1.14.ipsim"));
		final String saved=NetworkUtility.saveToString(context);
		return saved.length()!=0;
	}

	public String toString()
	{
		return "XMLSerialisationTest";
	}
}