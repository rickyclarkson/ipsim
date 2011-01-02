package ipsim.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class ContextMenuUtility {
    public static JMenuItem item(final String string, final char mnemonic, final Runnable action) {
        final JMenuItem item = new JMenuItem(string);
        item.setMnemonic(mnemonic);

        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                action.run();
            }
        });

        return item;
    }

    public static JMenuItem item(final String string, final char mnemonic, final int accelerator, final Runnable action) {
        final JMenuItem item = item(string, mnemonic, action);
        item.setAccelerator(KeyStroke.getKeyStroke(accelerator, InputEvent.CTRL_DOWN_MASK));
        return item;
    }
}
