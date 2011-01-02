package ipsim.lang;

import fj.F;

public class Doubles {
    public static <T> F<T, Integer> add(final F<T, Integer> first, final F<T, Integer> second) {
        return new F<T, Integer>() {
            @Override
            public Integer f(T t) {
                return first.f(t) + second.f(t);
            }
        };
    }
}
