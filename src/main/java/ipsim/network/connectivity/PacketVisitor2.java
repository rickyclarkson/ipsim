package ipsim.network.connectivity;

import ipsim.network.connectivity.arp.ArpPacket;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import ipsim.network.connectivity.ip.IPPacket;

public interface PacketVisitor2<R>
{
	R visit(IPPacket packet);

	R visit(ArpPacket packet);

	R visit(EthernetPacket packet);
}