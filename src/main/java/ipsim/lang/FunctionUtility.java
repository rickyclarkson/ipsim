package ipsim.lang;

import fj.F;
import org.jetbrains.annotations.NotNull;

public class FunctionUtility {
    public static <T> F<T,Boolean> not(final F<T,Boolean> function) {
        return new F<T, Boolean>() {
            @NotNull
            @Override
            public Boolean f(@NotNull T t) {
                return !function.f(t);
            }
        };
    }

    public static <T> F<T,Double> minus(final F<T,Double> f, final double amount) {
        return new F<T, Double>() {
            @Override
            public Double f(T t) {
                return f.f(t) - amount;
            }
        };
    }

    public static <T> F<T, Double> abs(final F<T, Double> f) {
        return new F<T, Double>() {
            @Override
            public Double f(T t) {
                return Math.abs(f.f(t));
            }
        };
    }

    public static <T> F<T, Boolean> lessThan(final F<T, Double> f, final int amount) {
        return new F<T, Boolean>() {
            @Override
            public Boolean f(T t) {
                return f.f(t) < amount;
            }
        };
    }

    public static <T> F<T,Boolean> and(final F<T, Boolean> one, final F<T, Boolean> two) {
        return new F<T, Boolean>() {
            @Override
            public Boolean f(T t) {
                return one.f(t) && two.f(t);
            }
        };
    }
}