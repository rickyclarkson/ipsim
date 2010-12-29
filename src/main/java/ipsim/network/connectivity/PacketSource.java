package ipsim.network.connectivity;

import ipsim.gui.components.PacketSourceVisitor2;

import java.util.List;

public interface PacketSource
{
	<R> R accept(PacketSourceVisitor<R> visitor);
	Listeners<IncomingPacketListener> getIncomingPacketListeners();
	Listeners<OutgoingPacketListener> getOutgoingPacketListeners();
	void accept(final PacketSourceVisitor2 visitor);
	List<PacketSourceAndIndex> children();
}