package ipsim.property;

import fpeas.sideeffect.SideEffect;
import ipsim.util.Collections;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public class PropertyUtility
{
	public static <T> Property<T> newProperty(@NotNull final T starting)
	{
		return new Property<T>()
		{
			public T value=starting;
			public final List<PropertyListener<T>> listeners=Collections.arrayList();
			public final Property<T> thiz=this;

			@Override
            @NotNull
			public T get()
			{
				return value;
			}

			@Override
            public void set(@NotNull final T newValue)
			{
				final T oldValue=value;
				value=newValue;
				Collections.forEach(listeners, update(oldValue, value));
			}

			private SideEffect<PropertyListener<T>> update(final T oldValue, final T value)
			{
				return new SideEffect<PropertyListener<T>>()
				{
					@Override
                    public void run(final PropertyListener<T> propertyListener)
					{
						propertyListener.propertyChanged(thiz, oldValue, value);
					}
				};
			}

			@Override
            public void addPropertyListener(@NotNull final PropertyListener<T> propertyListener)
			{
				listeners.add(propertyListener);
			}
		};
	}
}
