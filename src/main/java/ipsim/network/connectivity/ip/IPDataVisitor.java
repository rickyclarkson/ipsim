package ipsim.network.connectivity.ip;

import ipsim.network.connectivity.icmp.ping.PingData;
import ipsim.network.connectivity.icmp.ttl.TimeExceededData;
import ipsim.network.connectivity.icmp.unreachable.UnreachableData;

public interface IPDataVisitor
{
	void visit(PingData pingData);

	void visit(UnreachableData unreachableData);

	void visit(TimeExceededData data);
}