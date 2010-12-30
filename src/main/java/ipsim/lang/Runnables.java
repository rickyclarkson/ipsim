package ipsim.lang;

import fj.F;
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

	public static <T extends Exception,R> F<T,R> wrapAndThrow()
	{
		return new F<T,R>()
		{
			@Override
            @NotNull
			public R f(@NotNull final T input)
			{
				throw new RuntimeException(input);
			}
		};
	}

}