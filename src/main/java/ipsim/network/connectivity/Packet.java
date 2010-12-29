package ipsim.network.connectivity;

public interface Packet
{
	void accept(PacketVisitor visitor);

	<R> R accept(PacketVisitor2<R> visitor);
}