package com.rickyclarkson.javax.swing;

import fj.data.Either;
import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;

public final class ScrollableEditorPaneUtility {
    public static Either<JEditorPane, IOException> createScrollableEditorPane(final JScrollPane scrollPane, final URL initialPage) {
        final JEditorPane editorPane;
        try {
            editorPane = new JEditorPane(initialPage);
        } catch (IOException exception) {
            return Either.right(exception);
        }

        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final AWTEventListener keyHandler = new AWTEventListener() {
            @Override
            public void eventDispatched(final AWTEvent event) {
                if (!(event instanceof KeyEvent))
                    return;

                if (!event.getSource().equals(editorPane))
                    return;

                final KeyEvent keyEvent = (KeyEvent) event;

                if (!(keyEvent.getID() == KeyEvent.KEY_PRESSED))
                    return;

                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_UP:
                        break;

                    default:
                        return;
                }

                keyEvent.consume();

                final JViewport viewport = scrollPane.getViewport();

                final Rectangle rectangle = viewport.getViewRect().getBounds();

                final int unitIncrement = editorPane.getScrollableUnitIncrement(rectangle, SwingConstants.VERTICAL, 1);

                if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN)
                    rectangle.translate(0, unitIncrement);
                else
                    rectangle.translate(0, -unitIncrement);

                editorPane.scrollRectToVisible(rectangle);
            }
        };

        toolkit.addAWTEventListener(keyHandler, AWTEvent.KEY_EVENT_MASK);

        return Either.left(editorPane);
    }
}