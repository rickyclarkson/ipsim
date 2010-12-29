package ipsim.swing;

import static fpeas.maybe.MaybeUtility.just;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import fpeas.function.Function;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import org.jetbrains.annotations.NotNull;

public class Dialogs
{
	public static Maybe<JDialog> createDialogWithEscapeKeyToClose(final Maybe<JFrame> parent, final String title)
	{
		if (parent==null)
			return null;

		return MaybeUtility.constIfNothing(parent,MaybeUtility.<JDialog>nothing(),new Function<JFrame, Maybe<JDialog>>()
		{
			@Override
            @NotNull
			public Maybe<JDialog> run(@NotNull final JFrame jFrame)
			{
				return just(createDialogWithEscapeKeyToClose(jFrame,title));
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
