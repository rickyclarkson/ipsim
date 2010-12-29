package ipsim.lang;

/**
 Deprecate this sometime.
 */
public final class Assertion
{
	public static void assertTrue(final boolean... values)
	{
		for (final boolean value: values)
			if (!value)
				throw new AssertionException();
	}

	public static void assertFalse(final boolean... values)
	{
		for (final boolean value: values)
			if (value)
				throw new AssertionException();
	}

	public static void assertNotNull(final Object... objects)
	{
		for (final Object object: objects)
			assertFalse(object==null);
	}
}