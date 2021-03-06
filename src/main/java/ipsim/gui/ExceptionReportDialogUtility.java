package ipsim.gui;

import anylayout.AnyLayout;
import anylayout.extras.ConstraintUtility;
import anylayout.extras.PercentConstraints;
import anylayout.extras.PercentConstraintsUtility;
import com.rickyclarkson.java.lang.Throwables;
import ipsim.awt.ComponentUtility;
import ipsim.lang.Runnables;
import ipsim.swing.Buttons;
import ipsim.swing.Dialogs;
import ipsim.swing.ExceptionUploadListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ExceptionReportDialogUtility {
    public static void handle(final JFrame frame, final Throwable exception) {
        final JDialog dialog = Dialogs.createDialogWithEscapeKeyToClose(frame, "Error Report");
        dialog.setModal(true);

        dialog.setTitle("Error Report");

        final PercentConstraints constraints = PercentConstraintsUtility.newInstance(dialog.getContentPane());
        AnyLayout.useAnyLayout(dialog.getContentPane(), 0.5f, 0.5f, constraints.getSizeCalculator(), ConstraintUtility.typicalDefaultConstraint(Runnables.throwRuntimeException));

        constraints.add(new JLabel("An error has been detected."), 10, 10, 90, 5, false, false);

        constraints.add(new JLabel("You can ignore it, upload the event log to " + "our servers, or quit"), 10, 20, 90, 5, false, false);

        constraints.add(new JLabel("Error Summary:"), 10, 30, 30, 5, false, false);

        constraints.add(new JLabel(exception.toString()), 10, 35, 80, 5, false, false);

        constraints.add(new JLabel("Details:"), 10, 40, 80, 5, false, false);

        final JTextArea details = new JTextArea(Throwables.toString(exception));
        details.setEditable(false);
        details.setCaretPosition(0);

        constraints.add(new JScrollPane(details), 10, 45, 80, 30, true, true);

        constraints.add(Buttons.closeButton("Ignore", dialog), 10, 80, 20, 10, false, false);

        constraints.add(Buttons.newButton("Upload", new ExceptionUploadListener(exception)), 40, 80, 30, 10, false, false);

        constraints.add(Buttons.newButton("Quit", MenuHandler.fileExit()), 80, 80, 15, 10, false, false);

        dialog.setSize(3 * frame.getWidth() / 4, 3 * frame.getHeight() / 4);

        ComponentUtility.centreOnParent(dialog, frame);

        dialog.setVisible(true);
    }
}