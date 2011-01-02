package ipsim.awt;

import fj.F;
import org.jetbrains.annotations.NotNull;

public class Point {
    public final double x;
    public final double y;

    public Point(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }

    public F<Double, Point> withX() {
        return new F<Double, Point>() {
            @Override
            @NotNull
            public Point f(@NotNull final Double newX) {
                return new Point(newX, y);
            }
        };
    }

    public F<Double, Point> withY() {
        return new F<Double, Point>() {
            @Override
            @NotNull
            public Point f(@NotNull final Double newY) {
                return new Point(x, newY);
            }
        };
    }
}