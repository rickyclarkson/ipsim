package ipsim.gui.event;

import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.gui.components.RouteInfo;
import static ipsim.gui.event.PingCommand.pinged;
import ipsim.network.Network;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.connectivity.ping.PingResults;
import ipsim.network.ethernet.CardUtility;
import ipsim.network.ethernet.NetBlock;
import static ipsim.network.ethernet.NetBlockUtility.asCustomString;
import ipsim.network.ip.IPAddressUtility;

import java.util.List;

public final class CommandUtility
{
	public static String componentConnect(final PacketSource from, final PacketSource to, final Network network)
	{
		return "Connected "+PacketSourceUtility.asString(network, from)+" to "+PacketSourceUtility.asString(network, to)+'.';
	}

	public static String addDefaultRoute(final Computer computer, final IPAddress gateway, final Network network)
	{
		return "Added a default route to "+PacketSourceUtility.asString(network, computer)+" of "+IPAddressUtility.toString(gateway.rawValue)+'.';
	}

	public static String addExplicitRoute(final Computer computer, final NetBlock destination, final IPAddress gateway, final Network network)
	{
		return "Added an explicit route to "+PacketSourceUtility.asString(network, computer)+" to get to the "+asCustomString(destination)+" network, via the "+IPAddressUtility.toString(gateway.rawValue)+" gateway.";
	}

	public static String changedRoute(final Computer computer, final RouteInfo entry, final RouteInfo previous, final Network network)
	{
		return "Changed a route ("+previous.asString()+" to "+entry.asString()+" on "+PacketSourceUtility.asString(network, computer);
	}

	public static String createComponent(final PacketSource component, final Network network)
	{
		return "Created "+PacketSourceUtility.asString(network, component)+".";
	}

	public static String deleteComputer(final Computer computer, final Network network)
	{
		return "Deleted "+PacketSourceUtility.asString(network, computer);
	}

	public static String disableHubPower(final Hub hub, final Network network)
	{
		return "Disabled power on "+PacketSourceUtility.asString(network, hub)+'.';
	}

	public static String disableIpForwarding(final Computer computer, final Network network)
	{
		return "Disabled IP forwarding on "+PacketSourceUtility.asString(network, computer)+'.';
	}

	public static String enableHubPower(final Hub hub, final Network network)
	{
		return "Enabled power on "+PacketSourceUtility.asString(network, hub)+'.';
	}

	public static String enableIpForwarding(final Computer computer, final Network network)
	{
		return "Enabled IP forwarding on "+PacketSourceUtility.asString(network, computer)+'.';
	}

	public static String ipChange(final CardDrivers card, final IPAddress beforeIp, final NetMask beforeNetmask, final String cardBefore, final Network network)
	{

		final String after=IPAddressUtility.toString(card.ipAddress.get().rawValue);

		final String afterNetmask=NetMask.asString(card.netMask.get().rawValue);

		if (0==beforeIp.rawValue)
			return "Assigned IP address "+after+" and subnet mask "+afterNetmask+" to "+cardBefore+".";

		return "Changed the IP address of "+PacketSourceUtility.asString(network, card.card)+" from "+IPAddressUtility.toString(beforeIp.rawValue)+" to "+after+" and the netmask from "+NetMask.asString(beforeNetmask.rawValue)+" to "+afterNetmask+".";
	}

	public static String listIps(final Computer computer, final Network network)
	{
		return "Listed the IP addresses of "+PacketSourceUtility.asString(network, computer);
	}

	public static String listRoutingTable(final Computer computer, final Network network)
	{
		return "Listed the routing table of "+PacketSourceUtility.asString(network, computer);
	}

	public static String ping(final Computer computer, final IPAddress ipAddress, final List<PingResults> pingResults, final Network network)
	{
		return pinged(computer,ipAddress,pingResults,network);
	}

	public static String traceroute(final Computer computer, final IPAddress ipAddress, final int results, final Network network)
	{
		return "Tracerouted from "+PacketSourceUtility.asString(network, computer)+" to "+IPAddressUtility.toString(ipAddress.rawValue)+", "+results+" results received.";
	}

	public static String connectivityTested(final String text)
	{
		return "Tested the connectivity, "+text;
	}

	public static String scrapbookChecked(final String results)
	{
		return "Scrapbook - "+results;
	}

	public static String deleteRoute(final Computer computer, final String route, final Network network)
	{
		return "Deleted a route ("+route+") from "+PacketSourceUtility.asString(network, computer);
	}

	public static String removeIPAddress(final Computer computer, final CardDrivers card, final Network network)
	{
		return "Removed the "+asCustomString(CardUtility.getNetBlock(card))+" IP address from "+PacketSourceUtility.asString(network, computer);
	}
}