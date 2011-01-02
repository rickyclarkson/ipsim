package ipsim.network.connectivity;

import ipsim.lang.CheckedIllegalStateException;
import ipsim.network.connectivity.arp.ArpPacket;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import ipsim.network.connectivity.ip.IPPacket;
import org.jetbrains.annotations.Nullable;

public final class PacketUtility {
    public static IPPacket asIPPacket(final Packet packet) throws CheckedIllegalStateException {
        final PacketIdentifier identifier = new PacketIdentifier();
        packet.accept(identifier);
        return identifier.getIPPacket();
    }

    private static PacketIdentifier identifier(final Packet packet) {
        final PacketIdentifier identifier = new PacketIdentifier();
        packet.accept(identifier);
        return identifier;
    }

    public static
    @Nullable
    EthernetPacket asEthernetPacket(final Packet packet) throws CheckedIllegalStateException {
        return identifier(packet).asEthernetPacket();
    }

    public static ArpPacket asArpPacket(final Packet packet) throws CheckedIllegalStateException {
        return identifier(packet).asArpPacket();
    }
}