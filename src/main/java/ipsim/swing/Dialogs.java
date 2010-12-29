package ipsim.swing;

import fj.F;
import fj.data.Option;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

public class Dialogs
{
	public static Option<JDialog> createDialogWithEscapeKeyToClose(final Option<JFrame> parent, final String title)
	{
		if (parent==null)
			return null;

		return parent.map(new F<JFrame, JDialog>() {
            @Override
            public JDialog f(JFrame jFrame) {
                return createDialogWithEscapeKeyToClose(jFrame, title);
            }
        });
	}

	public static JDialog createDialogWithEscapeKeyToClose(final JFrame parent, final String title)
	{
		final JDialog result=new JDialog(parent,title);

		final ActionListener dispose=new ActionListener()
		{
			@Override
            public void actionPerformed(final ActionEvent event)
			{
				result.setVisible(false);
			}
		};

		result.getRootPane().registerKeyboardAction(dispose,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),JComponent.WHEN_IN_FOCUSED_WINDOW);
		return result;
	}
}
