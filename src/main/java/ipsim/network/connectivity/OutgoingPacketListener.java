package ipsim.network.connectivity;

public interface OutgoingPacketListener
{
	void packetOutgoing(Packet packet,PacketSource source);

	boolean canHandle(Packet packet,PacketSource source);
}