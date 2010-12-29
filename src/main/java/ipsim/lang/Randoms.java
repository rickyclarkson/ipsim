package ipsim.lang;

public class Randoms
{
	public static <T> T randomOneOf(final T... options)
	{
		return options[((int)(Math.random()*options.length))];
	}
}
