package ipsim.network.connectivity;

import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;

public interface PacketSourceVisitor<R>
{
	R visit(Card card);

	R visit(Computer computer);

	R visit(Cable cable);

	R visit(Hub hub);
}