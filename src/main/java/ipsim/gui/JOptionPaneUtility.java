package ipsim.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class JOptionPaneUtility {
    public static String showInputDialog(final JFrame mainFrame, final String message) {
        return JOptionPane.showInputDialog(mainFrame, message);
    }

    public static void showMessageDialog(final JFrame mainFrame, final String message) {
        JOptionPane.showMessageDialog(mainFrame, message);
    }

    public static int showConfirmDialog(final JFrame mainFrame, final Object message, final String title, final int messageType) {
        return JOptionPane.showConfirmDialog(mainFrame, message, title, messageType);
    }
}
