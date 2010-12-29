package ipsim.connectivity;

import com.rickyclarkson.testsuite.UnitTest;
import fpeas.function.Function;
import fpeas.sideeffect.SideEffectUtility;
import static ipsim.Caster.equalT;
import ipsim.Globals;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import static ipsim.network.NetworkUtility.getComputersByIP;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ping.PingResults;
import ipsim.network.connectivity.ping.Pinger;
import ipsim.network.ip.CheckedNumberFormatException;
import static ipsim.network.ip.IPAddressUtility.valueOf;
import static ipsim.util.Collections.all;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PingerTest implements UnitTest
{
	@Override
    public boolean invoke()
	{
		final Network network=new Network();

		NetworkUtility.loadFromFile(network,new File("datafiles/unconnected/pingertest1.ipsim"), SideEffectUtility.<IOException>throwRuntimeException());

		try
		{
			final IPAddress ip4_3=valueOf("146.87.4.3");
			final IPAddress ip4_1=valueOf("146.87.4.1");

			return all(getComputersByIP(network,valueOf("146.87.1.1")),new Function<Computer,Boolean>()
			{
				@Override
                @NotNull
				public Boolean run(@NotNull final Computer computer)
				{
					final List<PingResults> results=Pinger.ping(network,computer, new DestIPAddress(ip4_3), Globals.DEFAULT_TIME_TO_LIVE);

					if (1!=results.size())
						return false;

					return all(results,new Function<PingResults,Boolean>()
					{
						@Override
                        @NotNull
						public Boolean run(@NotNull final PingResults result)
						{
							final boolean answer=result.hostUnreachable()&&equalT(result.getReplyingHost().getIPAddress(),ip4_1);

							if (!answer)
								throw new RuntimeException(result.asString());

							return answer;
						}
					});
				}
			});
		}
		catch (final CheckedNumberFormatException exception)
		{
			return false;
		}
	}

	public String toString()
	{
		return "PingerTest";
	}
}
