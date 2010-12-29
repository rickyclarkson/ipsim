package ipsim.gui;

import static anylayout.AnyLayout.useAnyLayout;
import static anylayout.extras.ConstraintUtility.typicalDefaultConstraint;
import anylayout.extras.PercentConstraints;
import static anylayout.extras.PercentConstraintsUtility.newInstance;
import static anylayout.extras.SizeCalculatorUtility.constrain;
import ipsim.Global;
import ipsim.gui.event.LogUtility;
import static ipsim.lang.Runnables.throwRuntimeException;
import static ipsim.swing.Buttons.closeButton;
import static ipsim.swing.Dialogs.createDialogWithEscapeKeyToClose;

import javax.swing.*;

/**
 * Displays the log of events in a dialog.
 */
public final class EventLogDialogUtility
{
	public static JDialog createEventLogDialog()
	{
		final JDialog dialog=createDialogWithEscapeKeyToClose(Global.global.get().frame,"Event Log");

		dialog.setSize(800,400);
		dialog.setLocationRelativeTo(Global.global.get().frame);

		final PercentConstraints constraints=newInstance(dialog.getContentPane());
		useAnyLayout(dialog.getContentPane(),0.5f,0.5f,constrain(constraints.getSizeCalculator(),800,400),typicalDefaultConstraint(throwRuntimeException));

		mutatePane(constraints);

		constraints.add(closeButton("Close", dialog),43,90,15,10,false,false);

		final JButton closeButton=closeButton("Close", dialog);
		constraints.add(closeButton,43,90,15,10,false,false);
		dialog.pack();

		return dialog;
	}

	public static void mutatePane(final PercentConstraints constraints)
	{
		constraints.add(new JLabel("Event Log"),5,5,90,5,false,false);

		final JTextArea displayArea=new JTextArea(LogUtility.asString(Global.getNetworkContext().network.log));

		displayArea.setEditable(false);

		constraints.add(new JScrollPane(displayArea),5,10,90,75,true,true);
	}
}