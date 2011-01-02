package ipsim.network.connectivity;

public interface IncomingPacketListenerVisitor {
    void visit(PacketSniffer sniffer);

    void visit(IncomingPacketListener listener);
}