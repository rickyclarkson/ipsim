package ipsim.tests;

import com.rickyclarkson.testsuite.UnitTest;
import static ipsim.Caster.equalT;
import static ipsim.Global.global;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.loadFromFile;
import ipsim.network.conformance.ConformanceTests;
import ipsim.network.conformance.ConformanceTests.ResultsAndSummaryAndPercent;
import static ipsim.util.Collections.linkedHashMap;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

public class ConformanceTestsTest implements UnitTest
{
	@Override
    public boolean invoke()
	{
		final Map<String, Integer> fullyConnected=linkedHashMap();

		for (final String name : new String[]{"1.63","1.64"})
			fullyConnected.put(name, 12);

		for (final String name : new String[]{"broadcast1"})
			fullyConnected.put(name, 23);

		for (final String name : new String[]{"1.61"})
			fullyConnected.put(name, 39);

		for (final String name : new String[]{"101","arpforeign2"})
			fullyConnected.put(name, 50);

		for (final String name : new String[]{"1.5","1.6","1.7"})
			fullyConnected.put(name, 59);

		for (final String name : new String[]{"routingloop1","6"})
			fullyConnected.put(name, 81);

		for (final String name : new String[]{"arpforeign","1.4","1.52","1.58","5","1","arpfromip","simplest"})
			fullyConnected.put(name, 90);

		for (final String name : new String[]{"1-2-1","1-2","1.15","1.17","3","1.62","4"})
			fullyConnected.put(name, 100);

		final Map<String, Integer> unconnected=linkedHashMap();

		for (final String name : new String[]{"1","1.57","1.59","1.21","1.23"})
			unconnected.put(name, 0);

		for (final String name: new String[]{"1.48","1.43"})
			unconnected.put(name,5);

		for (final String name: new String[]{"1.44","1.26","1.27","1.37"})
			unconnected.put(name,12);

		for (final String name: new String[]{"1.50"})
			unconnected.put(name,13);

		for (final String name: new String[]{"1.56","1.38","1.52","1.39","1.41"})
			unconnected.put(name,15);

		for (final String name: new String[]{"1.34","1.60","1.51","101.2","1.14"})
			unconnected.put(name,18);

		for (final String name: new String[]{"1.10","1.32"})
			unconnected.put(name,20);

		unconnected.put("1.36", 25);

		for (final String name: new String[]{"1.33"})
			unconnected.put(name,30);

		for (final String name: new String[]{"1.40","1.42","1.16"})
			unconnected.put(name,33);

		unconnected.put("1.35", 34);
		unconnected.put("1.55", 35);

		for (final String name: new String[]{"1.24","1.54"})
			unconnected.put(name,36);

		unconnected.put("1.25", 41);
		unconnected.put("1.53",41);
		unconnected.put("1.54",41);

		for (final String name: new String[]{"arpitself","1.8"})
			unconnected.put(name,45);

		unconnected.put("1.18", 48);

		for (final String name: new String[]{"1.28","pingertest1","traceroute1"})
			unconnected.put(name,59);

		for (final String name: new String[]{"1.47","1.46"})
			unconnected.put(name,66);

		for (final String name: new String[]{"1.20","1.22","1.49"})
			unconnected.put(name,73);

		for (final String name: new String[]{"1.9","broadcast","broadcastaddress"})
			unconnected.put(name,81);

		for (final String name: new String[]{"1.12","1.13","1.19","hubdisabled"})
			unconnected.put(name,90);

		final Map<String, Integer> all=linkedHashMap();

		for (final Entry<String, Integer> entry : unconnected.entrySet())
			all.put("datafiles/unconnected/"+entry.getKey()+".ipsim", entry.getValue());

		for (final Entry<String, Integer> entry : fullyConnected.entrySet())
			all.put("datafiles/fullyconnected/"+entry.getKey()+".ipsim", entry.getValue());

		boolean passed=true;

		for (final Entry<String, Integer> entry : all.entrySet())
		{
			final Network network=new Network();

			final ResultsAndSummaryAndPercent conformanceTest=conformanceTest(network, entry.getKey());
			final int result=conformanceTest.percent;

			if (!equalT(entry.getValue(), result))
			{
				global.get().logger.severe(entry.getKey()+" should give "+entry.getValue()+", but actually gives "+result);
				passed=false;
			}
		}

		return passed;
	}

	private static ResultsAndSummaryAndPercent conformanceTest(final Network network, final String filename)
	{
		loadFromFile(network, new File(filename));

		return ConformanceTests.allChecks(network);
	}

	public String toString()
	{
		return "ConformanceTestsTest";
	}
}