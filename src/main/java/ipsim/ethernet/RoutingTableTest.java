package ipsim.ethernet;

import com.rickyclarkson.testsuite.UnitTest;
import fj.Effect;
import fj.P;
import fj.P2;
import fj.data.Option;
import ipsim.Caster;
import ipsim.network.Network;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.card.CardFactory;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.ComputerFactory;
import ipsim.network.connectivity.computer.NoSuchRouteException;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.computer.RoutingTable;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ethernet.NetBlockUtility;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;

import static ipsim.Caster.asNotNull;
import static ipsim.Caster.equalT;
import static ipsim.network.connectivity.computer.RoutingTableUtility.createRoutingTable;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getDefaultRoutes;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getRouteFor;
import static ipsim.network.ethernet.ComputerUtility.getCardFor;
import static ipsim.network.ethernet.NetBlockUtility.getBroadcastAddress;
import static ipsim.network.ip.IPAddressUtility.valueOf;

public final class RoutingTableTest
{
	public static final UnitTest testRetention=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final Network network=new Network();

			final Computer computer=ComputerFactory.newComputer(network, 100, 200);
			final RoutingTable table=createRoutingTable();
			computer.computerID=network.generateComputerID();

			final Card card=CardFactory.newCardConnectedTo(network, computer, 200, 300);
			card.installDeviceDrivers(network);

			final CardDrivers withDrivers=card.withDrivers;
			final Route route;
			try
			{
				withDrivers.ipAddress.set(IPAddressUtility.valueOf("146.87.1.1"));
				withDrivers.netMask.set(NetMaskUtility.createNetMaskFromPrefixLength(24));

				route=new Route(NetBlockUtility.getZero(), IPAddressUtility.valueOf("146.87.1.2"));
			}
			catch (final CheckedNumberFormatException exception)
			{
				return false;
			}

			table.add(Option.some(computer), route, Effect.<IPAddress>throwRuntimeException());

			return Caster.equalT(getDefaultRoutes(table).iterator().next(), route);
		}

		public String toString()
		{
			return "test retention";
		}
	};

	public static final UnitTest testGetBroadcastRoute=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final P2<Computer, P2<Network, CardDrivers>> setup=oneInstalledCardOnOneComputer();

			try
			{
				setup._2()._2().ipAddress.set(IPAddressUtility.valueOf("146.87.1.1"));
			}
			catch (final CheckedNumberFormatException exception1)
			{
				return false;
			}

			setup._2()._2().netMask.set(NetMaskUtility.createNetMaskFromPrefixLength(24));

			final Route route;
			try
			{
				route=getRouteFor(setup._1(), new DestIPAddress(valueOf("146.87.1.255")));
			}
			catch (final CheckedNumberFormatException exception)
			{
				throw new RuntimeException(exception);
			}
			catch (final NoSuchRouteException exception)
			{
				throw new RuntimeException(exception);
			}

			final CardDrivers card2=asNotNull(getCardFor(setup._1(), route));

			if (!equalT(setup._2()._2().card, card2.card))
				return false;

			final IPAddress broadcastAddress=getBroadcastAddress(route.block);

			try
			{
				return equalT(broadcastAddress, IPAddressUtility.valueOf("146.87.1.255"));
			}
			catch (final CheckedNumberFormatException exception)
			{
				return false;
			}
		}

		public String toString()
		{
			return "test getBroadcastRoute";
		}
	};

	public static P2<Computer, P2<Network, CardDrivers>> oneInstalledCardOnOneComputer()
	{
		final Network network=new Network();
		final Computer computer=ComputerFactory.newComputerWithID(network, 100, 200);
		final Card card=CardFactory.newCardConnectedTo(network, computer, 300, 200);
		card.installDeviceDrivers(network);
		return P.p(computer, P.p(network, card.withDrivers));
	}
}