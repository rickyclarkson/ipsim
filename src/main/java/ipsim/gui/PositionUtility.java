package ipsim.gui;

import com.rickyclarkson.testsuite.UnitTest;
import fpeas.maybe.Maybe;
import static fpeas.maybe.MaybeUtility.asJust;
import static fpeas.maybe.MaybeUtility.isJust;
import fpeas.pair.Pair;
import static fpeas.pair.PairUtility.pair;
import static fpeas.predicate.PredicateUtility.and;
import static ipsim.Caster.equalT;
import ipsim.NetworkContext;
import static ipsim.NetworkContext.errors;
import ipsim.awt.Point;
import ipsim.awt.PointUtility;
import static ipsim.awt.PointUtility.add;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import static ipsim.gui.GetChildOffset.getChildOffset;
import ipsim.lang.Assertion;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import static ipsim.network.connectivity.ConnectingTo.connectingTo;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketSourceAndIndex;
import static ipsim.network.connectivity.PacketSourceAndIndex.indexIs;
import static ipsim.network.connectivity.PacketSourceAndIndex.packetSourceIs;
import ipsim.network.connectivity.PacketSourceVisitor;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;
import ipsim.network.connectivity.hub.HubFactory;
import ipsim.util.Collections;
import static ipsim.util.Collections.arrayList;
import static ipsim.util.Collections.hashMap;
import static ipsim.util.Collections.mapWith;
import static ipsim.util.Collections.removeIf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class PositionUtility
{
	@NotNull
	public static Point getPosition(final Network network, final PacketSource object, final int index)
	{
		for (final PacketSourceAndPoints p : network.topLevelComponents)
			if (p.packetSource.equals(object) && p.pointsContainsKey(index))
				return p.pointsGet(index);

		for (final PacketSource p : NetworkUtility.getDepthFirstIterable(network))
			for (final PacketSourceAndIndex p2 : p.children())
				if (p2.packetSource.equals(object) && p2.index==index)
					return add(getPosition(network, p, 0), getChildOffset(p, object));

		throw new IllegalStateException("No position "+index+" for "+object.getClass());
	}

	public static int numPositions(final PacketSource object)
	{
		return object.accept(new PacketSourceVisitor<Integer>()
		{
			@Override
            public Integer visit(final Card card)
			{
				return 1;
			}

			@Override
            public Integer visit(final Computer computer)
			{
				return 1;
			}

			@Override
            public Integer visit(final Cable cable)
			{
				return 2;
			}

			@Override
            public Integer visit(final Hub hub)
			{
				return 1;
			}
		});
	}

	public static void removePositions(final Network network, final PacketSource component, @Nullable final NetworkView view)
	{
		for (final PacketSourceAndIndex p : new ArrayList<PacketSourceAndIndex>(component.children()))
			setPosition(network, p.packetSource, Collections.<Integer,Point>mapWith(p.index, getPosition(network, p.packetSource, p.index)));

		final List<PacketSourceAndPoints> toRemove=arrayList();
		for (final PacketSourceAndPoints p : network.topLevelComponents)
			if (p.packetSource.equals(component))
				toRemove.add(p);
			else
				new Object()
				{
					public void run(final Pair<List<PacketSourceAndIndex>, PacketSource> pair)
					{
						for (final PacketSourceAndIndex child : pair.first())
							run(pair(child.packetSource.children(), pair.second()));

						final Iterator<PacketSourceAndIndex> iterator=pair.first().iterator();

						while (iterator.hasNext())
							if (iterator.next().packetSource.equals(component))
								iterator.remove();
					}
				}.run(pair(p.packetSource.children(), component));

		network.topLevelComponents.removeAll(toRemove);

		if (view!=null)
			view.visibleComponents.remove(component);
	}

	public static void setPosition(final Network network, final PacketSource object, final Map<? extends Integer, ? extends Point> originalPositions)
	{
		final Map<Integer, Point> map=hashMap();

		for (final Map.Entry<? extends Integer,? extends Point> entry : originalPositions.entrySet())
		{
			final Point position=new Point(Math.max(entry.getValue().x, 0), Math.max(entry.getValue().y, 0));

			@Nullable final
			PacketSource parent=getParent(network, object, entry.getKey());

			if (parent!=null)
				removeIf(parent.children(), and(packetSourceIs(object), indexIs(entry.getKey())));

			for (final PacketSourceAndPoints p : network.topLevelComponents)
				if (equalT(p.packetSource, object))
				{
					network.topLevelComponents.add(new PacketSourceAndPoints(p.packetSource, p.pointsMapWith(entry.getKey(), position)));
					network.topLevelComponents.remove(p);
					return;
				}

			map.put(entry.getKey(), position);
		}

		network.topLevelComponents.add(new PacketSourceAndPoints(object, map));
	}

	public static final UnitTest testSetPosition=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final Network network=new Network();
			final Cable cable=network.cableFactory.newCable(50, 50, 100, 100);
			return getPosition(network, cable, 0).x==50;
		}

		public String toString()
		{
			return "testSetPosition";
		}
	};

	public static void setParent(final Network network, final PacketSource component, final int index, final PacketSource parent, final int index2)
	{
		if (hasParent(network, component, index))
			setPosition(network, component, mapWith(index, getPosition(network, component, index)));

		Maybe<String> possibleProblem=connectingTo(network, component, index, parent, index2);
		if (isJust(possibleProblem))
		{
			errors(asJust(possibleProblem));
			return;
		}

		possibleProblem=connectingTo(network, parent, index2, component, index);

		if (isJust(possibleProblem))
		{
			errors(asJust(possibleProblem));
			return;
		}

		for (final PacketSourceAndPoints p : Collections.copyOf(network.topLevelComponents, Collections.<PacketSourceAndPoints>arrayListRef()))
		{
			if (p.packetSource.equals(component))
			{
				Assertion.assertTrue(p.pointsContainsKey(index));

				if (!PacketSourceUtility.isCable(component) || hasParent(network,component,1-index))
					network.topLevelComponents.remove(p);

				break;
			}
		}

		parent.children().add(new PacketSourceAndIndex(component, index));

		network.modified=true;
	}

	@Nullable
	public static PacketSource getParent(final Network network, final PacketSource component, final int index)
	{
		return new Object()
		{
			@Nullable
			public PacketSource impl(final PacketSource component2, final int index2, final Iterable<PacketSourceAndIndex> components)
			{
				for (final PacketSourceAndIndex p : components)
				{
					for (final PacketSourceAndIndex child : p.packetSource.children())
						if (equalT(child.packetSource, component2) && index2==child.index)
							return p.packetSource;

					final PacketSource result2=impl(component2, index2, p.packetSource.children());
					if (result2!=null)
						return result2;
				}

				return null;
			}
		}.impl(component, index, getRootComponents(network));
	}

	public static void translateAll(final NetworkContext context, final int x, final int y)
	{
		final Point toAdd=new Point(x, y);

		//for each component, add toAdd to each point in its 'points'.

		for (final PacketSourceAndPoints packetSourceAndPoints : Collections.copyOf(context.network.topLevelComponents, Collections.<PacketSourceAndPoints>arrayListRef()))
		{
			context.network.topLevelComponents.remove(packetSourceAndPoints);
			context.network.topLevelComponents.add(new PacketSourceAndPoints(packetSourceAndPoints.packetSource, packetSourceAndPoints.pointsMapValues(PointUtility.add(toAdd))));
		}
	}

	public static Iterable<PacketSourceAndIndex> getRootComponents(final Network network)
	{
		final List<PacketSourceAndIndex> result=arrayList();
		for (final PacketSourceAndPoints p : network.topLevelComponents)
			for (final int key : p.pointsKeys())
				result.add(new PacketSourceAndIndex(p.packetSource, key));

		return result;
	}

	public static Point centreOf(final Network network, final PacketSource component)
	{
		final Map<Integer, Point> results=hashMap();

		for (int a=0;a<numPositions(component);a++)
			results.put(a, getPosition(network, component, a));

		int totalX=0;
		int totalY=0;

		for (final Point point : results.values())
		{
			totalX+=point.x;
			totalY+=point.y;
		}

		return new Point((double)totalX/results.size(), (double)totalY/results.size());
	}

	public static String getPositionOrParentAsString(final Network network, final PacketSource component, final int index)
	{
		final @Nullable PacketSource parent=getParent(network, component, index);

		if (parent!=null)
			return PacketSourceUtility.asString(network, parent);
		else
			return getPosition(network, component, index).toString();
	}

	public static void translateAllWhenNecessary(final NetworkContext context, final Rectangle visibleRect)
	{
		if (visibleRect.x<0 || visibleRect.y<0)
			translateAll(context, -Math.min(visibleRect.x, 0), -Math.min(visibleRect.y, 0));
	}

	public static boolean hasParent(final Network network, final PacketSource packetSource, final int index)
	{
		return getParent(network, packetSource, index)!=null;
	}

	public static final UnitTest testDeletingAHubWithBothEndsOfACableConnectedToIt=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final Network network=new Network();
			final Hub hub=HubFactory.newHub(network, 100, 100);
			final Cable cable=network.cableFactory.newCable(20, 20, 40, 40);
			setParent(network, cable, 0, hub, 0);
			setParent(network, cable, 1, hub, 0);
			removePositions(network, hub, null);
			getPosition(network, cable, 0);
			return true;
		}

		public String toString()
		{
			return "deleting a hub with both ends of a cable connected to it";
		}
	};

}