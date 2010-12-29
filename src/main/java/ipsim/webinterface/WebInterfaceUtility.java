package ipsim.webinterface;

import static ipsim.lang.Assertion.assertNotNull;
import static ipsim.lang.Assertion.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebInterfaceUtility
{
	public static boolean matchesExceptionResponse(final String string)
	{
		return string.matches("102: exception/log\\d+\\.\\d+\n"+"102: exception/save\\d+\\.\\d+\n");
	}

	public static String[] getLogAndSaveValues(final String output)
	{
		final Pattern pattern=Pattern.compile("102: exception/log(\\d+\\.\\d+)\n"+"102: exception/save(\\d+\\.\\d+)\n");

		final Matcher matcher=pattern.matcher(output);

		assertTrue(matcher.matches());
		assertNotNull(matcher.group(1),matcher.group(2));
		return new String[]{matcher.group(1),matcher.group(2)};
	}
}