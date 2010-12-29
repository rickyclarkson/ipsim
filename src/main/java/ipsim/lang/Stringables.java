package ipsim.lang;

import fpeas.function.Function;
import org.jetbrains.annotations.NotNull;

public class Stringables
{
	public static <T extends Stringable> Function<T,String> asString()
	{
		return new Function<T,String>()
		{
			@Override
            @NotNull
			public String run(@NotNull final T stringable)
			{
				return stringable.asString();
			}
		};
	}
}
