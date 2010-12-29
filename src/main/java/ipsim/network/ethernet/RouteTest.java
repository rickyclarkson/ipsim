package ipsim.network.ethernet;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.ethernet.RoutingTableTest;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Route;
import static ipsim.network.ethernet.NetBlockUtility.getZero;
import static ipsim.network.ethernet.RouteUtility.isDefaultRoute;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;
import static ipsim.network.ip.IPAddressUtility.valueOf;

public final class RouteTest implements UnitTest
{
	@Override
    public boolean invoke()
	{
		final CardDrivers card=RoutingTableTest.oneInstalledCardOnOneComputer().second().second();

		final Route route;
		try
		{
			card.ipAddress.set(IPAddressUtility.valueOf("146.87.1.1"));
			card.netMask.set(NetMaskUtility.createNetMaskFromPrefixLength(24));
			route=new Route(getZero(), valueOf("146.87.1.1"));
		}
		catch (final CheckedNumberFormatException exception)
		{
			return false;
		}

		return isDefaultRoute(route);
	}

	public String toString()
	{
		return "RouteTest";
	}
}