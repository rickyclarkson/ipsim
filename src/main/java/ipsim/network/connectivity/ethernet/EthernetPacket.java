package ipsim.network.connectivity.ethernet;

import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketVisitor;
import ipsim.network.connectivity.PacketVisitor2;

public class EthernetPacket implements Packet
{
	public final MacAddress sourceAddress;
	public final MacAddress destinationAddress;
	public final Packet data;

	public EthernetPacket(final MacAddress sourceAddress, final MacAddress destinationAddress, final Packet data)
	{
		this.sourceAddress=sourceAddress;
		this.destinationAddress=destinationAddress;
		this.data=data;
	}

	@Override
    public void accept(final PacketVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
    public <R> R accept(final PacketVisitor2<R> visitor)
	{
		return visitor.visit(this);
	}
}