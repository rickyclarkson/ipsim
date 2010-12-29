package ipsim.lang;

import fpeas.function.Function;

import java.util.Comparator;

public class Comparators
{

	public static <T> Comparator<T> fromFunction(final Function<T,Integer> function)
        {
		return new Comparator<T>()
		{
			@Override
            public int compare(final T o1, final T o2)
                        {
				return function.run(o1)-function.run(o2);
                        }
		};
        }
}
