package ipsim.util;

import com.rickyclarkson.testsuite.UnitTest;
import fj.F;
import fpeas.lazy.Lazy;
import fpeas.predicate.Predicate;
import fpeas.sideeffect.SideEffect;
import ipsim.lang.Stringable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Collections
{
	public static <T> List<T> arrayList()
	{
		return new ArrayList<T>();
	}

	public static <T extends Stringable> String asString(final Iterable<T> iterable)
	{
		final StringBuilder answer=new StringBuilder();

		boolean empty=true;

		for (final T item : iterable)
		{
			empty=false;
			answer.append(item.asString());
			answer.append(',');
		}

		if (!empty)
			answer.deleteCharAt(answer.length()-1);

		return answer.toString();
	}

	public static <K, V> Map<K, V> hashMap()
	{
		return new LinkedHashMap<K, V>();
	}

	public static <T> Collection<T> hashSet()
	{
		return new LinkedHashSet<T>();
	}

	public static <T> Stack<T> stack()
	{
		final java.util.Stack<T> real=new java.util.Stack<T>();

		return new Stack<T>()
		{
			@Override
            public void push(final T object)
			{
				real.push(object);
			}

			@Override
            public boolean isEmpty()
			{
				return real.isEmpty();
			}

			@Override
            public T pop()
			{
				return real.pop();
			}
		};
	}

	public static <T extends Stringable> String asString(final Iterable<T> stringables, final String separator)
	{
		final StringBuilder builder=new StringBuilder();
		boolean isEmpty=true;

		for (final Stringable stringable : stringables)
		{
			builder.append(stringable.asString());
			builder.append(separator);
			isEmpty=false;
		}

		if (!isEmpty)
			builder.delete(builder.length()-separator.length(), builder.length());

		return builder.toString();
	}

	public static <K, V> String asString(final Collection<Entry<K, V>> entrySet, final F<K, String> keyAsString, final F<V, String> valueAsString, final String betweenKeyAndValue, final String separator)
	{
		final StringBuilder builder=new StringBuilder();
		boolean isEmpty=true;

		for (final Entry<K, V> entry : entrySet)
		{
			builder.append(keyAsString.f(entry.getKey()));
			builder.append(betweenKeyAndValue);
			builder.append(valueAsString.f(entry.getValue()));
			builder.append(separator);
			isEmpty=false;
		}

		if (!isEmpty)
			builder.delete(builder.length()-separator.length(), builder.length());

		return builder.toString();
	}

	public static <T> int count(final Iterable<T> iterable, final Predicate<T> matcher)
	{
		int total=0;

		for (final T t : iterable)
			if (matcher.invoke(t))
				total++;

		return total;
	}

	public static <T> boolean any(final Iterable<? extends T> iterable, final Predicate<T> matcher)
	{
		for (final T item : iterable)
			if (matcher.invoke(item))
				return true;

		return false;
	}

	public static <T, R> Iterable<R> forEach(final Iterable<T> iterable, final F<T, R> runer)
	{
		final Collection<R> results=arrayList();

		for (final T item : iterable)
			results.add(runer.f(item));

		return results;
	}

	public static <T> boolean all(final Iterable<T> iterable, final F<? super T, Boolean> function)
	{
		for (final T item : iterable)
			if (!function.f(item))
				return false;

		return true;
	}

	public static <T,C extends Collection<T>> C only(final Lazy<C> construct,final Iterable<T> iterable,final Predicate<T> filter)
	{
		final C results=construct.invoke();

		for (final T t: iterable)
			if (filter.invoke(t))
				results.add(t);

		return results;
	}

	public static <T> Iterable<T> only(final Iterable<? extends T> iterable, final Predicate<T> filter)
	{
		final Collection<T> results=arrayList();

		for (final T item : iterable)
			if (filter.invoke(item))
				results.add(item);

		return results;
	}

	public static <T> String append(final Iterable<T> iterable, final F<T, String> runer)
	{
		final StringBuilder builder=new StringBuilder();

		for (final T item : iterable)
			builder.append(runer.f(item));

		return builder.toString();
	}

	public static <T> int sum(final F<T, Integer> function, final T... operands)
	{
		int result=0;

		for (final T item : operands)
			result+=function.f(item);

		return result;
	}

	public static <T> int max(final F<T, Integer> function, final T... operands)
	{
		int result=0;

		for (final T item : operands)
			result=Math.max(result, function.f(item));

		return result;
	}

	public static <T> List<T> sort2(final List<T> list, final Comparator<? super T> comparator)
	{
		final List<T> copy=arrayList();
		copy.addAll(list);
		java.util.Collections.sort(copy, comparator);
		return copy;
	}

	public static <K, V> Map<K, V> linkedHashMap()
	{
		return new LinkedHashMap<K, V>();
	}

	public static <T> int size(final Iterable<T> iterable)
	{
		int count=0;
		for (final T anIterable : iterable)
			count++;

		return count;
	}

	public static <T> SideEffect<T> add(final Collection<T> add)
	{
		return new SideEffect<T>()
		{
			@Override
            public void run(final T t)
			{
				add.add(t);
			}
		};
	}

	public static <T> List<T> arrayList(final Collection<? extends T> collection)
	{
		return new ArrayList<T>(collection);
	}

	public static <T> List<T> asList(final T... array)
	{
		return new ArrayList<T>(java.util.Arrays.asList(array));
	}

	public static <T, R> Iterable<R> map(final Iterable<T> input, final F<T, R> converter)
	{
		final Iterator<T> wrapped=input.iterator();

		return new Iterable<R>()
		{
			@Override
            public Iterator<R> iterator()
			{
				return new Iterator<R>()
				{
					@Override
                    public boolean hasNext()
					{
						return wrapped.hasNext();
					}

					@Override
                    public R next()
					{
						return converter.f(wrapped.next());
					}

					@Override
                    public void remove()
					{
						wrapped.remove();
					}
				};
			}
		};
	}

	public static <T> void removeIf(final Iterable<T> iterable, final Predicate<T> predicate)
	{
		final Iterator<T> iterator=iterable.iterator();
		while (iterator.hasNext())
			if (predicate.invoke(iterator.next()))
				iterator.remove();
	}

	public static <T> boolean all(final Iterable<T> iterable, final Predicate<T> predicate, final boolean defaultValue)
	{
		boolean result=defaultValue;

		for (final T t: iterable)
			if (!predicate.invoke(t))
				return false;
			else
				result=true;

		return result;
	}

	public static <T> void forEach(final Iterable<T> iterable, final SideEffect<T> effect)
	{
		for (final T t: iterable)
			effect.run(t);
	}

	public static <T> Lazy<List<T>> arrayListRef()
	{
		return new Lazy<List<T>>()
		{
			@Override
            public List<T> invoke()
			{
				return arrayList();
			}
		};
	}

	public static <T,C extends Collection<T>> C except(final Lazy<C> constructor,final Iterable<T> iterable,final Predicate<T> predicate)
	{
		final C c=constructor.invoke();

		for (final T t: iterable)
			if (!predicate.invoke(t))
				c.add(t);

		return c;
	}

	public static String join(final Iterable<String> iterable, final String separator)
	{
		final StringBuilder builder=new StringBuilder();

		boolean first=true;

		for (final String string: iterable)
		{
			if (!first)
				builder.append(separator);

			builder.append(string);

			first=false;
		}

		return builder.toString();
	}

	public static <K,V> String asString(final Map<K,V> map)
	{
		final StringBuilder builder=new StringBuilder("(");
		for (final Map.Entry<K,V> entry: map.entrySet())
			builder.append(" (").append(entry.getKey()).append(" . ").append(entry.getValue());
		return builder.append(')').toString();
	}

	public static <T,C extends Iterable<? extends T>,D extends Collection<T>> D add(final F<C,D> clone,final C collection, final T element)
	{
		final D result=clone.f(collection);
		result.add(element);
		return result;
	}

	public static final UnitTest testAddCollection=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final F<List<? extends String>, List<String>> clone=Collections.arrayListCopy();
			final List<String> list=arrayList();
			final Collection<? extends Object> objects=add(clone,add(clone, list, "hello"), "goodbye");
			return objects.contains("hello") && objects.contains("goodbye");
		}

		public String toString()
		{
			return "testAddCollection";
		}
	};

	public static <T> F<List<? extends T>, List<T>> arrayListCopy()
	{
		return new F<List<? extends T>, List<T>>()
		{
			@Override
            @NotNull
			public List<T> f(@NotNull final List<? extends T> ts)
			{
				return arrayList(ts);
			}
		};
	}

	public static <K,V> Map<? extends K, ? extends V> mapWith(final Map<? extends K, ? extends V> map, final K key, final V value)
	{
		final Map<K,V> result=new LinkedHashMap<K,V>(map);
		result.put(key,value);
		return result;
	}

	public static final UnitTest testMapWith=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final Map<? extends String, ? extends String> map=mapWith(new HashMap<String, String>()
			{
				{
					put("hello", "world");
				}
			}, "goodbye", "cruel");

			return map.get("goodbye").equals("cruel") && map.get("hello").equals("world");
		}

		public String toString()
		{
			return "testMapWith";
		}
	};
	public static <T,C extends Collection<T>> C copyOf(final Iterable<? extends T> iterable,final Lazy<C> constructor)
	{
		final C result=constructor.invoke();

		for (final T item: iterable)
			result.add(item);

		return result;
	}

	public static <K,V> Map<? extends K, ? extends V> mapValues(final Map<? extends K, ? extends V> map, final F<V, V> function)
	{
		final Map<K,V> result=hashMap();

		for (final Entry<? extends K, ? extends V> entry: map.entrySet())
			result.put(entry.getKey(),function.f(entry.getValue()));

		return result;
	}

	public static <T> Collection<? extends T> removeAll(final Collection<? extends T> collection, final Collection<T> toRemove)
	{
		final Collection<T> result=arrayList(collection);
		result.removeAll(toRemove);
		return result;
	}

	public static <T> Collection<? extends T> remove(final Collection<? extends T> collection, final T t)
	{
		final Collection<T> result=arrayList(collection);
		result.remove(t);
		return result;
	}

	public static <T> List<? extends T> add(final Collection<? extends T> collection, final T item)
	{
		final List<T> result=arrayList(collection);
		result.add(item);
		return result;
	}

	public static <K,V> Map<? extends K, ? extends V> mapWith(final K key,final V value)
	{
		return mapWith(Collections.<K,V>hashMap(),key,value);
	}

	@Nullable
	public static <T> T findIf(final Iterable<? extends T> iterable, final Predicate<T> predicate)
	{
		for (final T t: iterable)
			if (predicate.invoke(t))
				return t;

		return null;
	}

	public static <T> Iterable<T> concat(final Iterable<T> one, final Iterable<T> two)
	{
		return new Iterable<T>()
		{
			@Override
            public Iterator<T> iterator()
			{
				final Iterator<T> oneIt=one.iterator();
				final Iterator<T> twoIt=two.iterator();

				return new Iterator<T>()
				{
					@Override
                    public boolean hasNext()
					{
						return oneIt.hasNext() || twoIt.hasNext();
					}

					@Override
                    public T next()
					{
						return oneIt.hasNext() ? oneIt.next() : twoIt.next();
					}

					@Override
                    public void remove()
					{
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	public static final UnitTest testConcat=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			return size(concat(asList("one","two"),asList("three")))==3;
		}

		public String toString()
		{
			return "testConcat";
		}
	};
}