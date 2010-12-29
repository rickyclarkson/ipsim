package ipsim.util;

import ipsim.lang.Stringable;

public final class StackUtility
{
	public static <T extends Stringable> void pushAll
		(final Stack<T> stack,final Iterable<T> items)
	{
		for (final T item: items)
			stack.push(item);
	}
}