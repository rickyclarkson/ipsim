package ipsim.network.connectivity;

public interface IncomingPacketListener
{
	void packetIncoming(Packet packet,PacketSource source,PacketSource destination);

	boolean canHandle(Packet packet, PacketSource source);
}