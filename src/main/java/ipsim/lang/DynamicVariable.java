package ipsim.lang;

import com.rickyclarkson.testsuite.UnitTest;
import fpeas.lazy.Lazy;

public class DynamicVariable<T>
{
	private T t;

	public DynamicVariable(final T t)
	{
		this.t=t;
	}

	public T get()
	{
		return t;
	}

	public <R> R withValue(final T newT, final Lazy<R> lazy)
	{
		final T before=t;
		t=newT;
		try
		{
			return lazy.invoke();
		}
		finally
		{
			t=before;
		}
	}

	public static final UnitTest testDynamicVariable=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final DynamicVariable<String> var=new DynamicVariable<String>("hello");

			return var.withValue("goodbye",new Lazy<Boolean>()
			{
				@Override
                public Boolean invoke()
				{
					return var.get().equals("goodbye");
				}
			}) && var.get().equals("hello");
		}

		public String toString()
		{
			return "testDynamicVariable";
		}
	};
}