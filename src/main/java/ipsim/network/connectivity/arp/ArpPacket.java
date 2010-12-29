package ipsim.network.connectivity.arp;

import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketVisitor;
import ipsim.network.connectivity.PacketVisitor2;
import ipsim.network.connectivity.ethernet.MacAddress;
import ipsim.network.connectivity.ip.IPAddress;

public class ArpPacket implements Packet
{
	public final IPAddress destinationIPAddress;
	public final MacAddress destinationMacAddress;
	public final IPAddress sourceIPAddress;
	public final MacAddress sourceMacAddress;
	public final Object id;

	public ArpPacket(final IPAddress destinationIPAddress, final MacAddress destinationMacAddress, final IPAddress sourceIPAddress, final MacAddress sourceMacAddress, final Object id)
	{
		this.destinationIPAddress=destinationIPAddress;
		this.destinationMacAddress=destinationMacAddress;
		this.sourceIPAddress=sourceIPAddress;
		this.sourceMacAddress=sourceMacAddress;
		this.id=id;
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