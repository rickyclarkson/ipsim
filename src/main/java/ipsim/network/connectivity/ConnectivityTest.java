package ipsim.network.connectivity;

import fj.Effect;
import ipsim.Globals;
import ipsim.lang.Assertion;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.SourceIPAddress;
import ipsim.network.connectivity.ping.PingResults;
import ipsim.network.connectivity.ping.PingResultsUtility;
import ipsim.network.connectivity.ping.Pinger;
import ipsim.network.ethernet.ComputerUtility;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Nullable;

import static ipsim.Caster.asNotNull;
import static ipsim.Caster.equalT;
import static ipsim.network.ethernet.CardUtility.getNetBlock;
import static ipsim.network.ethernet.ComputerUtility.getTheFirstIPAddressYouCanFind;
import static ipsim.network.ethernet.NetBlockUtility.getBroadcastAddress;
import static ipsim.util.Collections.arrayList;

public final class ConnectivityTest
{
	private ConnectivityTest()
	{
	}

	public static ConnectivityResults testConnectivity(final Network network,final Effect<String> log, final Effect<Integer> progress)
	{
		final Collection<Computer> computers=NetworkUtility.getAllComputers(network);

		final List<String> results=arrayList();

		final int[] total={0};

		int currentComputer=0;

		for (final Computer computer: computers)
		{
			Assertion.assertNotNull(computer);

			@Nullable
			final IPAddress sourceIPAddress=getTheFirstIPAddressYouCanFind(computer);

			if (sourceIPAddress==null)
				continue;

			for (final Computer computer2: computers)
			{
				if (equalT(computer2,computer))
					continue;

				final List<CardDrivers> cards=ComputerUtility.cardsWithDrivers(computer2);

				for (final CardDrivers card: cards)
				{
					final IPAddress ipAddress=card.ipAddress.get();
					total[0]++;

					final boolean isBroadcast=equalT(getBroadcastAddress(getNetBlock(card)),ipAddress);

					final List<PingResults> pingResults=arrayList();

					for (final Computer anotherComputer: computers)
						anotherComputer.arpTable.clear();

					log.e("Pinging "+ipAddress.asString()+" from "+asNotNull(getTheFirstIPAddressYouCanFind(computer)).asString());

					final List<PingResults> tempPingResult=Pinger.ping(network,computer, new DestIPAddress(ipAddress), Globals.DEFAULT_TIME_TO_LIVE);
					pingResults.addAll(tempPingResult);

					final PingResults firstResult=pingResults.get(0);

					if (!firstResult.pingReplyReceived()||isBroadcast)
					{
						if (isBroadcast)
							pingResults.set(0, PingResultsUtility.hostUnreachable(new SourceIPAddress(sourceIPAddress)));

							results.add(sourceIPAddress.asString()+" cannot ping "+ipAddress.asString()+": "+firstResult.asString());
					}
				}
			}

			progress.e(currentComputer*100/computers.size());
			currentComputer++;
		}

		final int finalTotal=total[0];

		return new ConnectivityResults()
		{
			@Override
            public int getPercentConnected()
			{
				return (int)(100-results.size()*100.0/finalTotal);
			}

			@Override
            public Collection<String> getOutputs()
			{
				return results;
			}

			@Override
            public String asString()
			{
				final StringBuilder builder=new StringBuilder();

				builder.append(getPercentConnected()).append("% connected\n");

				for (final String string: results)
				{
					builder.append(string);
					builder.append('\n');
				}

				return builder.toString();
			}
		};
	}
}