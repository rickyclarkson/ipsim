package ipsim.awt;

import fpeas.function.Function;
import org.jetbrains.annotations.NotNull;

public class Point
{
	public final double x;
	public final double y;

	public Point(final double x, final double y)
	{
		this.x=x;
		this.y=y;
	}

	@Override
	public String toString()
	{
		return x+","+y;
	}

	public Function<Double,Point> withX()
	{
		return new Function<Double,Point>()
		{
			@Override
            @NotNull
			public Point run(@NotNull final Double newX)
			{
				return new Point(newX, y);
			}
		};
	}

	public Function<Double, Point> withY()
	{
		return new Function<Double,Point>()
		{
			@Override
            @NotNull
			public Point run(@NotNull final Double newY)
			{
				return new Point(x, newY);
			}
		};
	}
}