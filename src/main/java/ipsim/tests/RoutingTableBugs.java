package ipsim.tests;

import com.rickyclarkson.testsuite.UnitTest;
import fpeas.predicate.Predicate;
import ipsim.Caster;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.RoutingTableUtility;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.ComputerUtility;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;
import ipsim.util.Collections;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class RoutingTableBugs
{
	public static UnitTest loadingUnreachableRoutes()
	{
		return new UnitTest()
		{
			@Override
            public boolean invoke()
			{
				final Network network=new Network();
				NetworkUtility.loadFromFile(network,new File("datafiles/unconnected/1.14.ipsim"));

				final NetMask netMask;
				try
				{
					netMask=NetMaskUtility.valueOf("222.0.6.8");
				}
				catch (final CheckedNumberFormatException exception1)
				{
					return false;
				}

				try
				{
					final Collection<Computer> computers=NetworkUtility.getComputersByIP(network,IPAddressUtility.valueOf("10.0.0.1"));
					for (final Computer computer: computers)
					{
						final List<CardDrivers> possibleCards=Collections.only(Collections.<CardDrivers>arrayListRef(),ComputerUtility.cardsWithDrivers(computer),new Predicate<CardDrivers>()
						{
							@Override
                            public boolean invoke(final CardDrivers card)
							{
								return Caster.equalT(card.netMask.get(),netMask);
							}
						});

						if (!possibleCards.isEmpty())
							return RoutingTableUtility.getDefaultRoutes(computer.routingTable).iterator().hasNext();
					}
				}
				catch (final CheckedNumberFormatException exception)
				{
					return false;
				}

				return false;
			}

			public String toString()
			{
				return "RoutingTableBugs.loadingUnreachableRoutes";
			}

		};
	}
}