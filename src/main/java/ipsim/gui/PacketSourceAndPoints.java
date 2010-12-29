package ipsim.gui;

import fpeas.function.Function;
import ipsim.awt.Point;
import ipsim.network.connectivity.PacketSource;
import ipsim.util.Collections;
import static ipsim.util.Collections.mapValues;
import static ipsim.util.Collections.mapWith;

import java.util.Map;

public final class PacketSourceAndPoints
{
	public final PacketSource packetSource;
	private final Map<? extends Integer,? extends Point> points;

	public PacketSourceAndPoints(final PacketSource packetSource, final Map<? extends Integer,? extends Point> points)
	{
		this.packetSource=packetSource;
		this.points=points;
	}

	public boolean pointsContainsKey(final int index)
	{
		return points.containsKey(index);
	}

	public Point pointsGet(final int index)
	{
		return points.get(index);
	}

	public boolean pointsIsEmpty()
	{
		return points.isEmpty();
	}

	public Map<? extends Integer, ? extends Point> pointsMapWith(final int index, final Point position)
	{
		return mapWith(points,index,position);
	}

	public Map<? extends Integer, ? extends Point> pointsMapValues(final Function<Point, Point> function)
	{
		return mapValues(points,function);
	}

	public Iterable<Integer> pointsKeys()
	{
		return Collections.copyOf(points.keySet(),Collections.<Integer>arrayListRef());
	}

	public int pointsSize()
	{
		return points.size();
	}
}