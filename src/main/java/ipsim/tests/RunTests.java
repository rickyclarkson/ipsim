package ipsim.tests;

import static com.rickyclarkson.testsuite.UnitTestUtility.runTests;
import ipsim.connectivity.BroadcastPingTest;
import ipsim.connectivity.FullyConnectedFilesTest;
import ipsim.connectivity.PingerTest;
import ipsim.connectivity.RoutingLoopTest;
import ipsim.connectivity.UnconnectedFilesTest;
import ipsim.connectivity.computer.arp.incoming.ComputerArpIncomingTest;
import ipsim.connectivity.computer.arp.outgoing.ComputerArpOutgoingTest;
import ipsim.ethernet.ComputerTest;
import ipsim.ethernet.EthernetCableTest;
import ipsim.ethernet.RoutingTableTest;
import ipsim.gui.GetChildOffset;
import ipsim.gui.PositionUtility;
import ipsim.gui.PositionUtilityTest;
import ipsim.gui.NetworkView;
import static ipsim.lang.DynamicVariable.testDynamicVariable;
import ipsim.network.ProblemDifficulty;
import ipsim.network.ProblemTest;
import ipsim.network.Network;
import ipsim.network.conformance.HubWithMoreThanOneSubnet;
import ipsim.network.connectivity.cable.CableTest;
import ipsim.network.connectivity.traceroute.TracerouteTest;
import ipsim.network.ethernet.RouteTest;
import ipsim.persistence.LogRetentionTest;
import ipsim.persistence.delegates.ComputerDelegate;
import static ipsim.tests.CardAngle.testComputerWithTwoCards;
import static ipsim.tests.ManualTests.conformanceTestsShowLiteralHTML;
import static ipsim.tests.ManualTests.doesntSetTitle;
import ipsim.util.Collections;

import static java.lang.System.out;

public class RunTests
{
	public static void main(final String[] args)
	{
		System.setProperty("java.awt.headless","true");

		System.out.println("Running tests");

		final boolean results=runTests(out, Collections.testConcat,Network.testMergingNetworks,PositionUtilityTest.cableTopLevelAndChild,PositionUtilityTest.testRetention,PositionUtilityTest.cableWithTwoEnds,PositionUtilityTest.setParentTwice,PositionUtilityTest.testRetention2,NetworkView.testAddingComponentToANetworkAddsItToView,Collections.testMapWith,Collections.testAddCollection, ComputerDelegate.testSerialisationOfForwarding,new ConformanceTestsTest(),new XMLSerialisationTest(),testDynamicVariable,new LogRetentionTest(), PositionUtility.testDeletingAHubWithBothEndsOfACableConnectedToIt,ProblemDifficulty.testGeneration, GetChildOffset.test,CableTest.testCableWithNoParents, CableTest.testCable(),SerialisationTest.testSerialisingStrings,Bugzilla.bug18,new FullyConnectedFilesTest(),new ComputerArpIncomingTest(),RoutingTableTest.testGetBroadcastRoute,RoutingTableTest.testRetention,CardTests.testDrivers(),testComputerWithTwoCards(),HubWithMoreThanOneSubnet.testFalsePositiveWithZeroIP(),RoutingTableBugs.loadingUnreachableRoutes(),new InvalidRouteTest(),NetMaskTest.testEquality(),new BroadcastPingTest(),ProblemTest.instance(),new RemoteBroadcastBug(),new ArpStoredFromForeignNetworkBug(),new InfiniteLoopBug(),new EthernetCableTest(),new ComputerArpOutgoingTest(),new RouteTest(),ComputerTest.testCardIndexRetention,ComputerTest.testGetEth1,new RoutingLoopTest(),new PingerTest(),new TracerouteTest(),new UnconnectedFilesTest(),new IncompleteArpTest());

		final boolean expectedToFail=runTests(out, doesntSetTitle, conformanceTestsShowLiteralHTML);
		System.exit(results && !expectedToFail ? 0 : 1);
	}
}