package ipsim.gui.event;

import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ping.PingResults;
import ipsim.network.ip.IPAddressUtility;

import java.util.List;

public final class PingCommand
{
	public static String pinged(final Computer source, final IPAddress destination, final List<PingResults> pingResults, final Network network)
	{
		String string=PacketSourceUtility.asString(network,source);

		string=Character.toUpperCase(string.charAt(0))+string.substring(1);

		final StringBuilder tempDescription=new StringBuilder(string);
		tempDescription.append(" pinged ");
		tempDescription.append(IPAddressUtility.toString(destination.rawValue));
		tempDescription.append(", ");

		if (1==pingResults.size())
		{
			final PingResults results=pingResults.get(0);

			tempDescription.append(results==null ? "" : "with a result of \""+results.asString()+"\".");
		}
		else
		{
			tempDescription.append(pingResults.size());
			tempDescription.append(" results returned");
		}

		return tempDescription.toString();
	}
}