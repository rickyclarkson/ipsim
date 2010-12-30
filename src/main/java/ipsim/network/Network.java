package ipsim.network;

import com.rickyclarkson.testsuite.UnitTest;
import com.rickyclarkson.xml.DOMSimple;
import fj.F;
import fpeas.predicate.Predicate;
import ipsim.Caster;
import ipsim.ExceptionHandler;
import ipsim.awt.Point;
import ipsim.gui.PacketSourceAndPoints;
import ipsim.gui.components.EthernetCardHandler;
import ipsim.lang.Stringable;
import ipsim.network.connectivity.PacketQueue;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.cable.CableFactory;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.incoming.CardIncoming;
import ipsim.network.connectivity.card.outgoing.CardOutgoing;
import ipsim.network.connectivity.hub.Hub;
import ipsim.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import static ipsim.util.Collections.any;
import static ipsim.util.Collections.arrayList;
import static ipsim.util.Collections.hashMap;
import static ipsim.util.Collections.only;

public final class Network
{
	@Nullable
	public Problem problem=null;

	private Collection<? extends PacketSourceAndPoints> allComponents=arrayList();
	public List<? extends String> log=arrayList();

	public final PacketQueue packetQueue=new PacketQueue();
	public final List<PacketSource> macAddresses=arrayList();

	public int arpCacheTimeout=20;

	public final CableFactory cableFactory=new CableFactory(this);

	public boolean modified=false;

	public final F<Point, Card> cardFactory=new F<Point, Card>()
	{
		@Override
        @NotNull
		public Card f(@NotNull final Point point)
		{
			final Card card=EthernetCardHandler.create(Network.this, (int)point.x, (int)point.y);

			card.getIncomingPacketListeners().add(new CardIncoming(Network.this));
			card.getOutgoingPacketListeners().add(new CardOutgoing(Network.this));

			return card;
		}
	};
	public DOMSimple domSimple=new DOMSimple(ExceptionHandler.<String>impossibleRef(),ExceptionHandler.<Element>impossibleRef());

	public static <T> F<T, Integer> idFactory()
	{
		return new F<T, Integer>()
		{
			public final Map<Integer, T> ids=hashMap();

			@Override
            @NotNull
			public Integer f(@NotNull final T t)
			{
				for (final Map.Entry<Integer, T> entry : ids.entrySet())
					if (Caster.equalT(entry.getValue(), t))
						return entry.getKey();

				for (int a=0;;a++)
					if (!ids.containsKey(a))
					{
						ids.put(a, t);
						return a;
					}
			}
		};
	}

	public final F<Cable, Integer> cableIDFor=idFactory();
	public final F<Hub, Integer> hubIDFor=idFactory();

	public final TopLevelComponents topLevelComponents=new TopLevelComponents()
	{
		@Override
        public void add(final PacketSourceAndPoints element)
		{
			allComponents=Collections.add(allComponents,element);

			modified=true;
		}

		@Override
        public void removeAll(final List<PacketSourceAndPoints> toRemove)
		{
			allComponents=Collections.removeAll(allComponents,toRemove);
		}

		@Override
        public void remove(final PacketSourceAndPoints p)
		{
			allComponents=Collections.remove(allComponents,p);
		}

		@Override
        public void clear()
		{
			allComponents=arrayList();
		}

		@Override
        public boolean contains(final PacketSource source)
		{
			return any(allComponents,new Predicate<PacketSourceAndPoints>()
			{
				@Override
                public boolean invoke(final PacketSourceAndPoints packetSourceAndPoints)
				{
					return Caster.equalT(packetSourceAndPoints.packetSource,source);
				}
			});
		}

		@Override
        public int size()
		{
			return allComponents.size();
		}

		@Override
        public Iterator<PacketSourceAndPoints> iterator()
		{
			return only(allComponents, new Predicate<PacketSourceAndPoints>()
			{
				@Override
                public boolean invoke(final PacketSourceAndPoints packetSourceAndPoints)
				{
					return !packetSourceAndPoints.pointsIsEmpty();
				}
			}).iterator();
		}
	};

	public interface TopLevelComponents extends Iterable<PacketSourceAndPoints>
	{
		void add(PacketSourceAndPoints element);

		void removeAll(final List<PacketSourceAndPoints> toRemove);

		void remove(final PacketSourceAndPoints p);

		void clear();

		boolean contains(final PacketSource source);

		int size();
	}

	public final Map<Integer, Stringable> computerIDs=hashMap();
	public int nextComputerID=200;

	public Stringable createComputerID(@NotNull final String string)
	{
		final Stringable id=new Stringable()
		{
			@Override
            public String asString()
			{
				return string;
			}
		};

		computerIDs.put(Integer.parseInt(string), id);

		return id;
	}

	public Stringable generateComputerID()
	{
		final int tempNextComputerID=nextComputerID;

		final Stringable id=new Stringable()
		{
			@Override
            public String asString()
			{
				return String.valueOf(tempNextComputerID);
			}
		};

		computerIDs.put(tempNextComputerID, id);

		nextComputerID++;

		return id;
	}

	public static UnitTest testMergingNetworks=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final Network one=new Network();
			final Network two=new Network();
			one.cardFactory.f(new Point(50,50));
			two.cardFactory.f(new Point(100,100));
			final Network three=merge(one,two);
			return three.topLevelComponents.size()==2;
		}

		@Override
		public String toString()
		{
			return "testMergingNetworks";
		}
	};

	public static Network merge(final Network one, final Network two)
	{
		final Network three=new Network();

		for (final PacketSourceAndPoints x: Collections.concat(one.topLevelComponents,two.topLevelComponents))
			three.topLevelComponents.add(x);

		return three;
	}
}