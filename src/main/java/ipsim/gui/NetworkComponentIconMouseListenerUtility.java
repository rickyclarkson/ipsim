package ipsim.gui;

import ipsim.awt.Point;
import ipsim.gui.components.ComponentMoved;
import ipsim.gui.components.NetworkComponentUtility;
import ipsim.network.connectivity.PacketSource;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

import static ipsim.Global.getNetworkContext;

final class NetworkComponentIconMouseListenerUtility {
    public static MouseInputListener createNetworkComponentIconMouseListener(final Class<?> name) {
        final Cursor cursor = new Cursor(Cursor.MOVE_CURSOR);

        return new MouseInputListener() {
            @Override
            public void mouseDragged(final MouseEvent event) {
                getNetworkContext().networkView.setCursor(cursor);
            }

            @Override
            public void mouseReleased(final MouseEvent event) {
                final java.awt.Point buttonLocation = event.getComponent().getLocationOnScreen();

                final Component view = getNetworkContext().networkView;

                if (!view.isShowing())
                    return;

                final java.awt.Point viewLocation = view.getLocationOnScreen();

                int x = event.getX() + buttonLocation.x - viewLocation.x;
                int y = event.getY() + buttonLocation.y - viewLocation.y;

                x = Math.max(x, 10);
                y = Math.max(y, 10);

                x = Math.min(x, view.getWidth() - 10);
                y = Math.min(y, view.getHeight() - 10);

                x /= (int) getNetworkContext().zoomLevel;
                y /= (int) getNetworkContext().zoomLevel;

                final PacketSource component = NetworkComponentUtility.create(getNetworkContext(), name, new Point((double) x, (double) y), new Point((double) x, (double) y));

                ComponentMoved.componentMoved(getNetworkContext().network, component, 0);

                view.repaint();

                view.setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseClicked(final MouseEvent e) {
            }

            @Override
            public void mousePressed(final MouseEvent e) {
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
            }

            @Override
            public void mouseExited(final MouseEvent e) {
            }

            @Override
            public void mouseMoved(final MouseEvent e) {
            }
        };
    }
}
