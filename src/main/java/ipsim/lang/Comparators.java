package ipsim.lang;

import fj.F;
import java.util.Comparator;

public class Comparators {

    public static <T> Comparator<T> fromFunction(final F<T, Integer> function) {
        return new Comparator<T>() {
            @Override
            public int compare(final T o1, final T o2) {
                return function.f(o1) - function.f(o2);
            }
        };
    }
}
