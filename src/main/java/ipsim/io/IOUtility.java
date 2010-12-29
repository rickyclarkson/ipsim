package ipsim.io;

import fpeas.either.Either;
import fpeas.either.EitherUtility;
import fpeas.sideeffect.SideEffect;
import static ipsim.lang.Assertion.assertNotNull;

import java.io.*;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;

public final class IOUtility
{
	public static Either<String,IOException> readWholeResource(final URL resource)
	{
		try
		{
			InputStream inputStream=null;
			InputStreamReader inputStreamReader=null;
			BufferedReader reader=null;

			final StringBuilder builder=new StringBuilder();

			try
			{
				assertNotNull(resource);
				final URLConnection connection=resource.openConnection();
				connection.connect();

				inputStream=connection.getInputStream();
				inputStreamReader=new InputStreamReader(inputStream,"UTF-8");
				reader=new BufferedReader(inputStreamReader);

				boolean first=true;
				String string;

				do
				{
					string=reader.readLine();

					if (string!=null)
					{
						if (!first)
							builder.append('\n');

						builder.append(string);
						first=false;
					}
				}
				while (string!=null);

				reader.close();
			}
			finally
			{
				if (reader!=null)
					reader.close();

				if (inputStreamReader!=null)
					inputStreamReader.close();

				if (inputStream!=null)
					inputStream.close();
			}

			return EitherUtility.left(builder.toString());
		}
		catch (final IOException exception)
		{
			return EitherUtility.right(exception);
		}
	}

	public static void withPrintWriter(final Writer writer, final SideEffect<PrintWriter> sideEffect)
	{
		final PrintWriter pw=new PrintWriter(writer);
		try
		{
			sideEffect.run(pw);
		}
		finally
		{
			pw.close();
		}
	}
}