package ipsim.network.connectivity.arp;

public class ArpPacketUtility {
    public static boolean isRequest(final ArpPacket arpPacket) {
        return 0 == arpPacket.destinationMacAddress.rawValue;
    }
}