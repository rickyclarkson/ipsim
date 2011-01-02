package ipsim.gui;

import fj.Effect;
import ipsim.network.connectivity.hub.ProgressMonitor;

public class ProgressMonitorUtility
{
	public static Effect<String> setNote(final ProgressMonitor monitor)
	{
		return new Effect<String>()
		{
			@Override
            public void e(final String s)
			{
				monitor.setNote(s);
			}
		};
	}

	public static Effect<Integer> setProgress(final ProgressMonitor monitor)
	{
		return new Effect<Integer>()
		{
			@Override
            public void e(final Integer integer)
			{
				monitor.setProgress(integer);
			}
		};
	}
}