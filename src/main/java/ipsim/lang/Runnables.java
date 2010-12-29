package ipsim.lang;

import fpeas.function.Function;
import org.jetbrains.annotations.NotNull;

public class Runnables
{
	public static final Runnable throwRuntimeException=new Runnable()
	{
		@Override
        public void run()
		{
			throw new RuntimeException();
		}
	};

	public static final Runnable nothing=new Runnable()
	{
		@Override
        public void run()
		{
		}
	};

	public static <T extends Exception,R> Function<T,R> wrapAndThrow()
	{
		return new Function<T,R>()
		{
			@Override
            @NotNull
			public R run(@NotNull final T input)
			{
				throw new RuntimeException(input);
			}
		};
	}

}