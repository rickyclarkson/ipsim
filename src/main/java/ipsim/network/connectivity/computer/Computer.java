package ipsim.network.connectivity.computer;

import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asCard;
import ipsim.gui.components.PacketSourceVisitor2;
import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.Listeners;
import ipsim.network.connectivity.OutgoingPacketListener;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketSourceAndIndex;
import ipsim.network.connectivity.PacketSourceVisitor;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import static ipsim.network.connectivity.computer.RoutingTableUtility.createRoutingTable;
import ipsim.network.ethernet.CardComparator;
import ipsim.util.Collections;
import static ipsim.util.Collections.arrayList;
import ipsim.lang.Stringable;
import org.jetbrains.annotations.Nullable;

import static java.util.Collections.sort;
import java.util.List;

public final class Computer implements PacketSource
{
	public boolean ipForwardingEnabled=false;

	public final ArpTable arpTable;

	public final RoutingTable routingTable;

	private final Listeners<IncomingPacketListener> incomingPacketListeners=new Listeners<IncomingPacketListener>();

	private final Listeners<OutgoingPacketListener> outgoingPacketListeners=new Listeners<OutgoingPacketListener>();

	@Nullable
	public Stringable computerID=null;

	private final List<PacketSourceAndIndex> children=arrayList();

	public Computer()
	{
		arpTable=new ArpTable();

		routingTable=createRoutingTable();
	}

	public List<CardDrivers> getSortedCards()
	{
		final List<CardDrivers> list=Collections.arrayList();

		for (final PacketSourceAndIndex child: children)
		{
			final Card card=asCard(child.packetSource);

			if (card!=null)
			{
				final CardDrivers withDrivers=card.withDrivers;
				if (withDrivers!=null)
					list.add(withDrivers);
			}
		}

		sort(list,new CardComparator());

		return java.util.Collections.unmodifiableList(list);
	}

	public List<Card> getCards()
	{
		final List<PacketSourceAndIndex> handlers=children();

		final List<Card> cards=Collections.arrayList();

		for (final PacketSourceAndIndex component: handlers)
		{
			final Card card=asCard(component.packetSource);
			cards.add(card);
		}

		return cards;
	}

	public int getFirstAvailableEthNumber()
	{
		final List<CardDrivers> cards=getSortedCards();

		for (int a=0;;a++)
		{
			boolean foundConflict=false;

			for (final CardDrivers card: cards)
				if (a==card.ethNumber)
				{
					foundConflict=true;
					break;
				}

			if (!foundConflict)
				return a;
		}
	}

	@Override
	public String toString()
	{
		throw new UnsupportedOperationException();
	}

	@Override
    public List<PacketSourceAndIndex> children()
	{
		return children;
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
    public void accept(final PacketSourceVisitor2 visitor)
	{
		visitor.visit(this);
	}

	@Override
    public <R> R accept(final PacketSourceVisitor<R> visitor)
	{
		return visitor.visit(this);
	}

	public boolean isISP=false;
}