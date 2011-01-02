package ipsim.swing;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class JDialogUtility {
    public static void setLocationRelativeTo(final JDialog dialog, final JFrame parent) {
        dialog.setLocationRelativeTo(parent);
    }

}