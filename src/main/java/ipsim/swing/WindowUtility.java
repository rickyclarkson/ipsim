package ipsim.swing;

import javax.swing.JDialog;
import java.awt.event.WindowListener;

public class WindowUtility
{
	public static void pack(final JDialog window)
	{
		window.pack();
	}

	public static void addWindowListener(final JDialog wrapped, final WindowListener listener)
	{
		wrapped.addWindowListener(listener);
	}
}