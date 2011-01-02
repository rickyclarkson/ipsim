package ipsim.gui.components.initialdialog;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JRadioButton;

public class InitialDialog {
    public final JDialog dialog;

    public InitialDialog(final JDialog dialog, final JButton okButton, final JRadioButton practiceTestRadioButton) {
        this.dialog = dialog;
    }
}