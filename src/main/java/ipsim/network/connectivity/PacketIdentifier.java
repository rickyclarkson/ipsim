package ipsim.network.connectivity;

import ipsim.lang.CheckedIllegalStateException;
import ipsim.network.connectivity.arp.ArpPacket;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import ipsim.network.connectivity.ip.IPPacket;

final class PacketIdentifier implements PacketVisitor {
    private IPPacket ipPacket;
    private EthernetPacket ethernetPacket;
    private ArpPacket arpPacket;

    @Override
    public void visit(final IPPacket packet) {
        clearAll();
        ipPacket = packet;
    }

    private void clearAll() {
        ipPacket = null;
    }

    public IPPacket getIPPacket() throws CheckedIllegalStateException {
        if (ipPacket == null)
            throw new CheckedIllegalStateException();

        return ipPacket;
    }

    @Override
    public void visit(final ArpPacket packet) {
        clearAll();
        arpPacket = packet;
    }

    @Override
    public void visit(final EthernetPacket packet) {
        clearAll();
        ethernetPacket = packet;
    }

    public EthernetPacket asEthernetPacket() throws CheckedIllegalStateException {
        if (ethernetPacket == null)
            throw new CheckedIllegalStateException();

        return ethernetPacket;
    }

    public ArpPacket asArpPacket() throws CheckedIllegalStateException {
        if (arpPacket == null)
            throw new CheckedIllegalStateException();

        return arpPacket;
    }

}