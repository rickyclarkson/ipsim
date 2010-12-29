package ipsim.gui.components;

import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.hub.Hub;
import org.jetbrains.annotations.NotNull;

public interface PacketSourceVisitor2
{
	void visit(@NotNull Card card);
	void visit(@NotNull Computer computer);
	void visit(@NotNull Cable cable);
	void visit(@NotNull Hub hub);
}