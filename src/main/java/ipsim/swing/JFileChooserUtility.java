package ipsim.swing;

import javax.swing.*;

public class JFileChooserUtility
{
	public static int showSaveDialog(final JFileChooser fileChooser, final JFrame realFrame)
	{
		return fileChooser.showSaveDialog(realFrame);
	}
}