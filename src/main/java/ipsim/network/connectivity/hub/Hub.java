package ipsim.network.connectivity.hub;

import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.gui.components.PacketSourceVisitor2;
import ipsim.network.Network;
import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.Listeners;
import ipsim.network.connectivity.OutgoingPacketListener;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketSourceAndIndex;
import ipsim.network.connectivity.PacketSourceVisitor;
import ipsim.network.connectivity.cable.Cable;
import ipsim.util.Collections;
import static ipsim.util.Collections.arrayList;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class Hub implements PacketSource
{
	private boolean powerStatus=false;

	private final Listeners<IncomingPacketListener> incomingPacketListeners=new Listeners<IncomingPacketListener>();

	private final Listeners<OutgoingPacketListener> outgoingPacketListeners=new Listeners<OutgoingPacketListener>();
	private final Network network;
	private final List<PacketSourceAndIndex> children=arrayList();

	public Hub(final Network network)
	{
		this.network=network;
	}

	public void setPower(final boolean status)
	{
		powerStatus=status;

		network.modified=true;
	}

	public boolean isPowerOn()
	{
		return powerStatus;
	}

	@Override
    public void accept(final PacketSourceVisitor2 visitor)
	{
		visitor.visit(this);
	}

	@Override
    public List<PacketSourceAndIndex> children()
	{
		return children;
	}

	public Collection<Cable> getCables()
	{
		final Collection<Cable> cables=Collections.hashSet();

		for (final PacketSourceAndIndex element: children)
		{
			@Nullable final Cable cable=PacketSourceUtility.asCable(element.packetSource);

			if (cable!=null)
				cables.add(cable);
		}

		return cables;
	}

	@Override
	public String toString()
	{
		throw new UnsupportedOperationException();
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

	@Override
    public <R> R accept(final PacketSourceVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}