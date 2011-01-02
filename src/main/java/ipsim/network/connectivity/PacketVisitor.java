package ipsim.network.connectivity;

import ipsim.network.connectivity.arp.ArpPacket;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import ipsim.network.connectivity.ip.IPPacket;
import org.jetbrains.annotations.NotNull;

public interface PacketVisitor {
    void visit(@NotNull IPPacket packet);

    void visit(@NotNull ArpPacket packet);

    void visit(@NotNull EthernetPacket packet);
}