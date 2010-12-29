package ipsim.awt;

import javax.swing.*;

public final class ComponentUtility
{
	/**
	 * Used to centre the dialogs.
	 */
	public static void centreOnParent(final JDialog dialog,final JFrame frame)
	{
		int x=frame.getX();
		x+=(frame.getWidth()-dialog.getWidth())/2;

		int y=frame.getY();
		y+=(frame.getHeight()-dialog.getHeight())/2;

		dialog.setLocation(x,y);
	}
}