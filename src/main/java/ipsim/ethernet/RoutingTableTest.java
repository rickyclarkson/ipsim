package ipsim.ethernet;

import static ipsim.Caster.asNotNull;
import com.rickyclarkson.testsuite.UnitTest;
import static fpeas.maybe.MaybeUtility.just;
import fpeas.pair.Pair;
import static fpeas.pair.PairUtility.pair;
import fpeas.sideeffect.SideEffectUtility;
import ipsim.Caster;
import static ipsim.Caster.equalT;
import ipsim.network.Network;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardFactory;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.ComputerFactory;
import ipsim.network.connectivity.computer.NoSuchRouteException;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.computer.RoutingTable;
import static ipsim.network.connectivity.computer.RoutingTableUtility.createRoutingTable;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getDefaultRoutes;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getRouteFor;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import static ipsim.network.ethernet.ComputerUtility.getCardFor;
import ipsim.network.ethernet.NetBlockUtility;
import static ipsim.network.ethernet.NetBlockUtility.getBroadcastAddress;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;
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

			table.add(just(computer), route, SideEffectUtility.<IPAddress>throwRuntimeException());

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
			final Pair<Computer, Pair<Network, CardDrivers>> setup=oneInstalledCardOnOneComputer();

			try
			{
				setup.second().second().ipAddress.set(IPAddressUtility.valueOf("146.87.1.1"));
			}
			catch (final CheckedNumberFormatException exception1)
			{
				return false;
			}

			setup.second().second().netMask.set(NetMaskUtility.createNetMaskFromPrefixLength(24));

			final Route route;
			try
			{
				route=getRouteFor(setup.first(), new DestIPAddress(valueOf("146.87.1.255")));
			}
			catch (final CheckedNumberFormatException exception)
			{
				throw new RuntimeException(exception);
			}
			catch (final NoSuchRouteException exception)
			{
				throw new RuntimeException(exception);
			}

			final CardDrivers card2=asNotNull(getCardFor(setup.first(), route));

			if (!equalT(setup.second().second().card, card2.card))
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

	public static Pair<Computer, Pair<Network, CardDrivers>> oneInstalledCardOnOneComputer()
	{
		final Network network=new Network();
		final Computer computer=ComputerFactory.newComputerWithID(network, 100, 200);
		final Card card=CardFactory.newCardConnectedTo(network, computer, 300, 200);
		card.installDeviceDrivers(network);
		return pair(computer, pair(network, card.withDrivers));
	}
}