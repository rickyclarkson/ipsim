package ipsim.network.connectivity.ping;

import ipsim.Caster;
import static ipsim.lang.Assertion.assertFalse;
import ipsim.lang.CheckedIllegalStateException;
import ipsim.network.Network;
import ipsim.network.connectivity.PacketQueue;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.NoSuchRouteException;
import ipsim.network.connectivity.computer.Route;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getRouteFor;
import static ipsim.network.connectivity.computer.RoutingTableUtility.hasRouteFor;
import ipsim.network.connectivity.icmp.ping.PingData;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.IPPacket;
import ipsim.network.connectivity.ip.SourceIPAddress;
import static ipsim.network.connectivity.ping.PingResultsUtility.hostUnreachable;
import static ipsim.network.connectivity.ping.PingResultsUtility.netUnreachable;
import static ipsim.network.ethernet.ComputerUtility.getCardFor;
import static ipsim.network.ethernet.ComputerUtility.getTheFirstIPAddressYouCanFind;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;
import ipsim.util.Collections;
import org.jetbrains.annotations.Nullable;

import static java.util.Collections.singletonList;
import java.util.List;

public final class Pinger
{
	public static List<PingResults> ping(final Network network,final Computer computer,final DestIPAddress ipAddress,final int ttl)
	{
		final boolean hasRoute=hasRouteFor(computer,ipAddress);

		final Object identifier=new Object();

		final PingListener pingListener=new PingListener(identifier);

		computer.getIncomingPacketListeners().add(pingListener);

		try
		{
			@Nullable
			final IPAddress ipAddress1=getTheFirstIPAddressYouCanFind(computer);

			if (ipAddress1==null)
				try
				{
					return singletonList(netUnreachable(new SourceIPAddress(IPAddressUtility.valueOf("127.0.0.1"))));
				}
				catch (final CheckedNumberFormatException exception)
				{
					throw new RuntimeException(exception);
				}

			final SourceIPAddress aRandomSourceIP=new SourceIPAddress(ipAddress1);

			if (!hasRoute)
				return singletonList(netUnreachable(aRandomSourceIP));

			final Route route;
			try
			{
				route=getRouteFor(computer,ipAddress);
			}
			catch (final NoSuchRouteException exception)
			{
				throw new RuntimeException(exception);
			}

			@Nullable
			final CardDrivers card=getCardFor(computer,route);

			if (card==null)
				return singletonList(netUnreachable(aRandomSourceIP));

			assertFalse(0==card.ipAddress.get().rawValue);

			final IPPacket packet=new IPPacket(new SourceIPAddress(card.ipAddress.get()), ipAddress, ttl, identifier,PingData.REQUEST);

			final PacketQueue packetQueue=network.packetQueue;
			packetQueue.enqueueOutgoingPacket(packet,computer);

			packetQueue.processAll();

			try
			{
				pingListener.getPingResults();
			}
			catch (final CheckedIllegalStateException exception)
			{
				pingListener.timedOut(new SourceIPAddress(card.ipAddress.get()));
			}

			final List<PingResults> pingResults=Collections.arrayList();

			try
			{
				pingResults.addAll(pingListener.getPingResults());
			}
			catch (final CheckedIllegalStateException exception)
			{
				throw new RuntimeException(exception);
			}

			for (int a=0;a<pingResults.size();a++)
			{
				final PingResults result=pingResults.get(a);

				if (result.timedOut()&& Caster.equalT(route.gateway).invoke(card.ipAddress.get()))
					pingResults.set(a,hostUnreachable(result.getReplyingHost()));
			}

			return pingResults;
		}
		finally
		{
			computer.getIncomingPacketListeners().remove(pingListener);
		}
	}
}