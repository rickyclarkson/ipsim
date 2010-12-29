package ipsim.ethernet;

import com.rickyclarkson.testsuite.UnitTest;
import fpeas.function.Function;
import static ipsim.gui.PositionUtility.setParent;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import static ipsim.network.NetworkUtility.getComputersByIP;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.ComputerFactory;
import ipsim.network.connectivity.ip.IPAddress;
import static ipsim.network.ethernet.ComputerUtility.getEth;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;
import static ipsim.util.Collections.all;
import ipsim.awt.Point;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class ComputerTest
{
	public static final UnitTest testCardIndexRetention=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final Network network=new Network();

			final Computer computer=ComputerFactory.newComputer(network, 0, 0);
			computer.computerID=network.generateComputerID();

			final Card card0=network.cardFactory.run(new Point(0, 0));

			final Card card1=network.cardFactory.run(new Point(0, 0));

			setParent(network, card0, 0, computer, 0);
			setParent(network, card1, 0, computer, 0);

			card0.installDeviceDrivers(network);
			final CardDrivers withDrivers0=card0.withDrivers;
			card1.installDeviceDrivers(network);

			return 0==withDrivers0.ethNumber;
		}

		public String toString()
		{
			return "card index retention";
		}
	};

	public static final UnitTest testGetEth1=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final Network network=new Network();

			NetworkUtility.loadFromFile(network, new File("datafiles/unconnected/1.60.ipsim"));

			final IPAddress ipAddress;
			try
			{
				ipAddress=IPAddressUtility.valueOf("146.87.1.1");
			}
			catch (final CheckedNumberFormatException exception)
			{
				return false;
			}

			return all(getComputersByIP(network, ipAddress), new Function<Computer, Boolean>()
			{
				@Override
                @NotNull
				public Boolean run(@NotNull final Computer computer)
				{
					return getEth(computer, 1)!=null;
				}
			});
		}

		public String toString()
		{
			return "testGetEth1";
		}
	};
}