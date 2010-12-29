package ipsim.gui;

import fpeas.sideeffect.SideEffect;
import ipsim.network.connectivity.hub.ProgressMonitor;

public class ProgressMonitorUtility
{
	public static SideEffect<String> setNote(final ProgressMonitor monitor)
	{
		return new SideEffect<String>()
		{
			@Override
            public void run(final String s)
			{
				monitor.setNote(s);
			}
		};
	}

	public static SideEffect<Integer> setProgress(final ProgressMonitor monitor)
	{
		return new SideEffect<Integer>()
		{
			@Override
            public void run(final Integer integer)
			{
				monitor.setProgress(integer);
			}
		};
	}
}