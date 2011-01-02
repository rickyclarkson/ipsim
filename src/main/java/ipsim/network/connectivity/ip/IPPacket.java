package ipsim.network.connectivity.ip;

import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketVisitor;
import ipsim.network.connectivity.PacketVisitor2;
import ipsim.network.connectivity.icmp.IcmpData;

public final class IPPacket implements Packet {
    public final SourceIPAddress sourceIPAddress;
    public final DestIPAddress destinationIPAddress;
    public final int timeToLive;
    public final IcmpData data;
    public final Object identifier;

    public IPPacket(final SourceIPAddress sourceIPAddress, final DestIPAddress destinationIPAddress, final int timeToLive, final Object identifier, final IcmpData data) {
        this.sourceIPAddress = sourceIPAddress;
        this.destinationIPAddress = destinationIPAddress;
        this.timeToLive = timeToLive;
        this.identifier = identifier;
        this.data = data;
    }

    @Override
    public void accept(final PacketVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <R> R accept(final PacketVisitor2<R> visitor) {
        return visitor.visit(this);
    }
}