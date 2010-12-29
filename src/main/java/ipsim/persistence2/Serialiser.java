package ipsim.persistence2;

import java.io.PrintWriter;

public class Serialiser
{
	public final PrintWriter out;

	public Serialiser(final PrintWriter out)
	{
		this.out=out;
	}

	public void write(final String text)
	{
		out.print('"');
		out.print(backslashEscape(text));
		out.print('"');
		out.print(' ');
	}

	private static String backslashEscape(final String text)
	{
		return text.replaceAll("\"","\\\\\"");
	}

	public void enter()
	{
		out.print("(");
	}

	public void exit()
	{
		out.print(") ");
	}
}