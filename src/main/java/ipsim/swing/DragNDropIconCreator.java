package ipsim.swing;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Container;
import java.awt.Cursor;

public final class DragNDropIconCreator
{
	public static Container newInstance(final ImageIcon icon,final String text)
	{
		final JButton button=new JButton(text,icon);
		button.setVerticalAlignment(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		final String an=text.startsWith("E") ? "n" : "";

		button.setToolTipText("<html><center>&nbsp;Drag from this button&nbsp;<br>&nbsp;to create a"+an+" "+text+"&nbsp;</center></html>");
		button.setFocusable(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		return button;
	}
}