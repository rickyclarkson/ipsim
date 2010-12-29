package ipsim.connectivity;

import fpeas.function.Function;
import ipsim.Globals;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.getComputersByIP;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ping.PingResults;
import ipsim.network.connectivity.ping.Pinger;
import ipsim.util.Collections;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public class PingTester
{
	public static Iterable<List<PingResults>> testPing(final Network network,final IPAddress sourceIP,final IPAddress destIP)
	{
		return Collections.forEach(getComputersByIP(network,sourceIP),new Function<Computer,List<PingResults>>()
		{
			@Override
            @NotNull
			public List<PingResults> run(@NotNull final Computer computer)
			{
				return Pinger.ping(network,computer, new DestIPAddress(destIP), Globals.DEFAULT_TIME_TO_LIVE);
			}
		});
	}
}