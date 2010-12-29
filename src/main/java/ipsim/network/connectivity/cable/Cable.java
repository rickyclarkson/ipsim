package ipsim.network.connectivity.cable;

import static ipsim.gui.PositionUtility.getParent;
import ipsim.gui.components.PacketSourceVisitor2;
import ipsim.network.Network;
import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.Listeners;
import ipsim.network.connectivity.OutgoingPacketListener;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketSourceAndIndex;
import ipsim.network.connectivity.PacketSourceVisitor;
import static ipsim.network.connectivity.cable.CableType.STRAIGHT_THROUGH;
import ipsim.util.Collections;
import org.jetbrains.annotations.Nullable;

import static java.util.Collections.emptyList;
import java.util.Iterator;
import java.util.List;

public final class Cable implements PacketSource
{
	private final Listeners<IncomingPacketListener> incomingPacketListeners=new Listeners<IncomingPacketListener>();

	private final Listeners<OutgoingPacketListener> outgoingPacketListeners=new Listeners<OutgoingPacketListener>();

	private CableType cableType=STRAIGHT_THROUGH;

	public List<PacketSource> getEnds(final Network network)
	{
		final List<PacketSource> ends=Collections.arrayList();

		for (int end=0;end<2;end++)
		{
			final @Nullable PacketSource parent=getParent(network, this, end);
			if (parent!=null)
				ends.add(parent);
		}

		return ends;
	}

	public boolean canTransferPackets(final Network network)
	{
		final List<PacketSource> ends=getEnds(network);

		if (ends.size()!=2)
			return false;

		final Iterator<PacketSource> iterator=ends.iterator();
		return cableType.canTransferPackets(iterator.next(),iterator.next());
	}

	@Override
    public void accept(final PacketSourceVisitor2 visitor)
	{
		visitor.visit(this);
	}

	@Override
    public List<PacketSourceAndIndex> children()
	{
		return emptyList();
	}
	
	@Override
    public Listeners<IncomingPacketListener> getIncomingPacketListeners()
	{
		return incomingPacketListeners;
	}

	@Override
    public Listeners<OutgoingPacketListener> getOutgoingPacketListeners()
	{
		return outgoingPacketListeners;
	}

	public CableType getCableType()
	{
		return cableType;
	}

	public void setCableType(final CableType cableType)
	{
		this.cableType=cableType;
	}

	@Override
    public <R> R accept(final PacketSourceVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}