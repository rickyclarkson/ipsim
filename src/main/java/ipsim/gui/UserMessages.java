package ipsim.gui;

import ipsim.Global;
import static ipsim.gui.JOptionPaneUtility.showMessageDialog;

public class UserMessages
{

	public static void message(final String message)
	{
		showMessageDialog(Global.global.get().frame,message);
	}
}