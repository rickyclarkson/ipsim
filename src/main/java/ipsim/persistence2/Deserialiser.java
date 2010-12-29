package ipsim.persistence2;

import java.io.IOException;
import java.io.Reader;

public class Deserialiser
{
	public final Reader reader;

	public Deserialiser(final Reader reader)
	{
		this.reader=reader;
	}

	private void expectChar(final char ch)
	{
		final int read;
		try
		{
			read=reader.read();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		if (read!=ch)
			throw new IllegalStateException(String.valueOf((char)read));
	}
	public String readString()
	{
		expectChar('"');

		final StringBuilder result=new StringBuilder();

		while (true)
			try
			{
				final int ch=reader.read();

				if (ch=='"')
				{
					reader.read();
					return result.toString();
				}

				if (ch==-1)
					return result.toString();

				if (ch=='\\')
					result.append((char)reader.read());

				result.append((char)ch);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
	}

	public void enter()
	{
		expectChar('(');
	}

	public void exit()
	{
		expectChar(')');
	}
}