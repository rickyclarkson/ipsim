package ipsim.tests;

import com.rickyclarkson.testsuite.UnitTest;
import fj.data.Option;
import fpeas.sideeffect.SideEffect;
import ipsim.awt.Point;
import ipsim.network.Network;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.ComputerFactory;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.computer.RoutingTableUtility;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ethernet.NetBlockUtility;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;
import ipsim.util.Collections;

import static ipsim.gui.PositionUtility.setParent;

public class InvalidRouteTest implements UnitTest
{
	@Override
    public boolean invoke()
	{
		try
		{
			return testChangingIPToMakeRouteInvalid()&&testProgrammaticallyAddingInvalidRoute();
		}
		catch (final CheckedNumberFormatException exception)
		{
			return false;
		}
	}

	private static boolean testProgrammaticallyAddingInvalidRoute() throws CheckedNumberFormatException
	{
		final Network network=new Network();
		final Computer computer=ComputerFactory.newComputer(network, 200,200);
		computer.computerID=network.generateComputerID();
		final Route route=new Route(NetBlockUtility.getZero(), IPAddressUtility.valueOf("146.87.1.1"));

		final boolean[] passed={false};

		computer.routingTable.add(Option.some(computer),route,new SideEffect<IPAddress>()
		{
			@Override
            public void run(final IPAddress input)
			{
				passed[0]=true;
			}
		});

		return passed[0];
	}

	public static boolean testChangingIPToMakeRouteInvalid() throws CheckedNumberFormatException
	{
		final Network network=new Network();
		final Computer computer=ComputerFactory.newComputer(network, 200,200);
		computer.computerID=network.generateComputerID();
		final Card card=network.cardFactory.f(new Point(300, 300));
		setParent(network,card,0,computer,0);
		card.installDeviceDrivers(network);
		final CardDrivers withDrivers=card.withDrivers;
		withDrivers.ipAddress.set(IPAddressUtility.valueOf("146.87.1.1"));
		withDrivers.netMask.set(NetMaskUtility.valueOf("255.255.255.0"));
		final Route route=new Route(NetBlockUtility.getZero(), IPAddressUtility.valueOf("146.87.1.2"));

		final boolean[] passed={true};

		computer.routingTable.add(Option.some(computer),route,new SideEffect<IPAddress>()
		{
			@Override
            public void run(final IPAddress input)
			{
				passed[0]=false;
			}
		});

		if (!passed[0])
			return false;

		withDrivers.ipAddress.set(IPAddressUtility.valueOf("146.87.2.1"));
		return Collections.size(RoutingTableUtility.getDefaultRoutes(computer.routingTable))==0;
	}

	public String toString()
	{
		return "InvalidRouteTest";
	}
}