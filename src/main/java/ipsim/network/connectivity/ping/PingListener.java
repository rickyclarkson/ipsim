package ipsim.network.connectivity.ping;

import fpeas.function.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import fpeas.predicate.Predicate;
import static ipsim.Caster.equalT;
import ipsim.lang.CheckedIllegalStateException;
import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketUtility;
import ipsim.network.connectivity.PacketUtility2;
import ipsim.network.connectivity.icmp.IcmpData;
import static ipsim.network.connectivity.icmp.ping.PingData.REPLY;
import static ipsim.network.connectivity.icmp.ttl.TimeExceededData.TIME_TO_LIVE_EXCEEDED;
import static ipsim.network.connectivity.icmp.unreachable.UnreachableData.HOST_UNREACHABLE;
import static ipsim.network.connectivity.icmp.unreachable.UnreachableData.NET_UNREACHABLE;
import ipsim.network.connectivity.ip.IPPacket;
import ipsim.network.connectivity.ip.SourceIPAddress;
import static ipsim.network.connectivity.ping.PingResultsUtility.hostUnreachable;
import static ipsim.network.connectivity.ping.PingResultsUtility.netUnreachable;
import static ipsim.network.connectivity.ping.PingResultsUtility.pingReplyReceived;
import static ipsim.network.connectivity.ping.PingResultsUtility.ttlExpired;
import static ipsim.util.Collections.arrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class PingListener implements IncomingPacketListener
{
	private final Object identifier;
	private final List<PingResults> pingResults=arrayList();

	public PingListener(final Object identifier)
	{
		this.identifier=identifier;
	}

	@Override
    public void packetIncoming(final Packet packet,final PacketSource source,final PacketSource destination)
	{
		final IPPacket ipPacket;
		try
		{
			ipPacket=PacketUtility.asIPPacket(packet);
		}
		catch (final CheckedIllegalStateException exception)
		{
			throw new RuntimeException(exception);
		}

		final Predicate<IcmpData> equalT=equalT(ipPacket.data);

		if (equalT.invoke(TIME_TO_LIVE_EXCEEDED))
			pingResults.add(ttlExpired(ipPacket.sourceIPAddress));

		if (equalT.invoke(REPLY))
			pingResults.add(pingReplyReceived(ipPacket.sourceIPAddress));

		if (equalT.invoke(NET_UNREACHABLE))
			pingResults.add(netUnreachable(ipPacket.sourceIPAddress));

		if (equalT.invoke(HOST_UNREACHABLE))
			pingResults.add(hostUnreachable(ipPacket.sourceIPAddress));
	}

	@Override
    public boolean canHandle(final Packet packet, final PacketSource source)
	{
		final Maybe<IPPacket> maybe=PacketUtility2.asIPPacket(packet);
		return MaybeUtility.constIfNothing(maybe,false,new Function<IPPacket,Boolean>()
		{
			@Override
            @NotNull
			public Boolean run(@NotNull final IPPacket ipPacket)
			{
				return equalT(ipPacket.identifier,identifier);
			}
		});
	}

	public List<PingResults> getPingResults() throws CheckedIllegalStateException
	{
		if (pingResults.isEmpty())
			throw new CheckedIllegalStateException();

		return pingResults;
	}

	public void timedOut(final SourceIPAddress address)
	{
		if (pingResults.isEmpty())
			pingResults.add(PingResultsUtility.timedOut(address));
	}
}