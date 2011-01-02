package ipsim.swing;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public final class Buttons {
    public static JButton closeButton(final String caption, final Window window) {
        final JButton button = new JButton(caption);
        button.addActionListener(new CloseListener(window));
        return button;
    }

    public static JButton newButton(final String string, final Runnable runnable) {
        final JButton button = new JButton(string);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                runnable.run();
            }
        });

        return button;
    }

    public static JButton newButton(final String caption, final ActionListener listener) {
        final JButton button = new JButton(caption);
        button.addActionListener(listener);
        return button;
    }
}

final class CloseListener implements ActionListener {
    private final Window window;

    CloseListener(final Window window) {
        this.window = window;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        window.setVisible(false);
        window.dispose();
    }
}