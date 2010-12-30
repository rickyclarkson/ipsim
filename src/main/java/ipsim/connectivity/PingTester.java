package ipsim.connectivity;

import fj.F;
import ipsim.Globals;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ping.PingResults;
import ipsim.network.connectivity.ping.Pinger;
import ipsim.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.NetworkUtility.getComputersByIP;

public class PingTester
{
	public static Iterable<List<PingResults>> testPing(final Network network,final IPAddress sourceIP,final IPAddress destIP)
	{
		return Collections.forEach(getComputersByIP(network,sourceIP),new F<Computer,List<PingResults>>()
		{
			@Override
            @NotNull
			public List<PingResults> f(@NotNull final Computer computer)
			{
				return Pinger.ping(network,computer, new DestIPAddress(destIP), Globals.DEFAULT_TIME_TO_LIVE);
			}
		});
	}
}