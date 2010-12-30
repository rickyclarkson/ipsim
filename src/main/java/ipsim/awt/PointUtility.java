package ipsim.awt;

import fj.F;
import org.jetbrains.annotations.NotNull;

public final class PointUtility
{
	public static final Point origin=new Point((double)0, (double)0);

	public static Point add(final Point one,final Point two)
	{
		return new Point(one.x+two.x, one.y+two.y);
	}

	public static Point div(final Point point,final double divisor)
	{
		return new Point(point.x/divisor, point.y/divisor);
	}

	public static Point between(final Point one,final Point two)
	{
		return div(add(one,two),2);
	}

	public static F<Point,Point> add(final Point toAdd)
	{
		return new F<Point, Point>()
		{
			@Override
            @NotNull
			public Point f(@NotNull final Point point)
			{
				return add(point,toAdd);
			}
		};
	}
}