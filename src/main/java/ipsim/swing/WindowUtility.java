package ipsim.swing;

import java.awt.event.WindowListener;
import javax.swing.JDialog;

public class WindowUtility {
    public static void pack(final JDialog window) {
        window.pack();
    }

    public static void addWindowListener(final JDialog wrapped, final WindowListener listener) {
        wrapped.addWindowListener(listener);
    }
}