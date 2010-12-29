package ipsim.gui.event;

import java.util.List;

public class LogUtility
{
	public static String asString(final List<? extends String> log)
	{
		final StringBuilder answer=new StringBuilder();

		for (final String command : log)
			answer.append(command).append('\n');

		return answer.toString();
	}
}