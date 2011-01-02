package ipsim.network.connectivity.ping;

import fj.F;
import fj.data.Option;
import ipsim.lang.CheckedIllegalStateException;
import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketUtility;
import ipsim.network.connectivity.PacketUtility2;
import ipsim.network.connectivity.icmp.IcmpData;
import ipsim.network.connectivity.ip.IPPacket;
import ipsim.network.connectivity.ip.SourceIPAddress;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import static ipsim.Caster.equalT;
import static ipsim.network.connectivity.icmp.ping.PingData.REPLY;
import static ipsim.network.connectivity.icmp.ttl.TimeExceededData.TIME_TO_LIVE_EXCEEDED;
import static ipsim.network.connectivity.icmp.unreachable.UnreachableData.HOST_UNREACHABLE;
import static ipsim.network.connectivity.icmp.unreachable.UnreachableData.NET_UNREACHABLE;
import static ipsim.network.connectivity.ping.PingResultsUtility.hostUnreachable;
import static ipsim.network.connectivity.ping.PingResultsUtility.netUnreachable;
import static ipsim.network.connectivity.ping.PingResultsUtility.pingReplyReceived;
import static ipsim.network.connectivity.ping.PingResultsUtility.ttlExpired;
import static ipsim.util.Collections.arrayList;

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

		final F<IcmpData, Boolean> equalT=equalT(ipPacket.data);

		if (equalT.f(TIME_TO_LIVE_EXCEEDED))
			pingResults.add(ttlExpired(ipPacket.sourceIPAddress));

		if (equalT.f(REPLY))
			pingResults.add(pingReplyReceived(ipPacket.sourceIPAddress));

		if (equalT.f(NET_UNREACHABLE))
			pingResults.add(netUnreachable(ipPacket.sourceIPAddress));

		if (equalT.f(HOST_UNREACHABLE))
			pingResults.add(hostUnreachable(ipPacket.sourceIPAddress));
	}

	@Override
    public boolean canHandle(final Packet packet, final PacketSource source)
	{
		final Option<IPPacket> maybe=PacketUtility2.asIPPacket(packet);
		return maybe.map(new F<IPPacket,Boolean>()
		{
			@Override
            @NotNull
			public Boolean f(@NotNull final IPPacket ipPacket)
			{
				return equalT(ipPacket.identifier,identifier);
			}
		}).orSome(false);
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