package ipsim.network.connectivity.icmp;

import ipsim.network.connectivity.ip.IPDataVisitor;
import ipsim.network.connectivity.IPDataVisitor2;

public interface IcmpData
{
	void accept(IPDataVisitor visitor);
	<T> T accept(IPDataVisitor2<T> visitor);
}