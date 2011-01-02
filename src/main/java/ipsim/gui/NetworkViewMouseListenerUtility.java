package ipsim.gui;

import fj.F;
import fj.data.Option;
import ipsim.NetworkContext;
import ipsim.awt.Point;
import ipsim.gui.NetworkViewUtility.PointRecordDead;
import ipsim.gui.components.ComponentMoved;
import ipsim.gui.components.InvokeContextMenu;
import ipsim.network.connectivity.PacketSource;
import ipsim.util.Collections;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;
import org.jetbrains.annotations.NotNull;

import static ipsim.gui.PositionUtility.setPosition;
import static ipsim.gui.PositionUtility.translateAllWhenNecessary;
import static ipsim.gui.components.CreateContextMenu.createContextMenu;

public final class NetworkViewMouseListenerUtility {
    public static MouseInputListener createNetworkViewMouseListener(final NetworkContext context) {
        return new MouseInputListener() {
            public NetworkViewUtility.PointRecordDead startDrag = null;

            public int changedX = 0;

            public int changedY = 0;

            @Override
            public void mouseClicked(final MouseEvent originalEvent) {
                final MouseEvent event = zoomMouseEvent(originalEvent);

                context.mouseTracker.mouseEvent(event);
            }

            public void popupTriggered(final MouseEvent event) {
                if (event.isPopupTrigger()) {
                    final PacketSource component = NetworkViewUtility.getPointAt(context.network, event.getX(), event.getY());

                    if (component != null)
                        InvokeContextMenu.invokeContextMenu(createContextMenu(component), context, component);
                }
            }

            MouseEvent zoomMouseEvent(final MouseEvent event) {
                final double zoomLevel = context.zoomLevel;

                return new MouseEvent(event.getComponent(), event.getID(), event.getWhen(), event.getModifiers(), (int) (event.getX() / zoomLevel), (int) (event.getY() / zoomLevel), event.getClickCount(), event.isPopupTrigger(), event.getButton());
            }

            /**
             * Keeps track of where the mouse drag was started from, and renders the object that was closest to the starting position, giving the impression that the object (or part of it) is being dragged across the display.
             */
            @Override
            public void mouseDragged(MouseEvent originalEvent) {
                changedX = 0;
                changedY = 0;

                context.mouseTracker.mouseEvent(originalEvent); //swapped this and the next
                originalEvent = zoomMouseEvent(originalEvent);


                final JComponent view = context.networkView;

                if (startDrag == null && originalEvent.getButton() == MouseEvent.BUTTON1)
                    startDrag = NetworkViewUtility.getTopLevelPointAt(context.network, originalEvent.getX(), originalEvent.getY());

                if (startDrag == null)
                    return;

                setPosition(context.network, startDrag.object, Collections.mapWith(startDrag.index, new Point((double) originalEvent.getX(), (double) originalEvent.getY())));

                final Rectangle visibleRect = view.getVisibleRect();

                if (!(changedX == 0) || !(changedY == 0)) {
                    translateAllWhenNecessary(context, visibleRect);

                    view.scrollRectToVisible(visibleRect);
                }

                jiggleLayout();
                view.invalidate();
                view.validate();
                view.repaint();
            }

            @Override
            public void mouseEntered(final MouseEvent event) {
                context.mouseTracker.mouseEvent(event);
            }

            @Override
            public void mouseExited(final MouseEvent event) {
                context.mouseTracker.mouseEvent(event);
            }

            @Override
            public void mouseMoved(final MouseEvent event) {
                context.mouseTracker.mouseEvent(event);
                context.networkView.repaint();
            }

            @Override
            public void mousePressed(final MouseEvent originalEvent) {
                final MouseEvent event = zoomMouseEvent(originalEvent);

                context.mouseTracker.mouseEvent(originalEvent);

                final PointRecordDead point = NetworkViewUtility.getTopLevelPointAt(context.network, event.getX(), event.getY());

                if (event.isPopupTrigger())
                    popupTriggered(event);
                else if (event.getButton() == MouseEvent.BUTTON1)
                    startDrag = point;

                context.networkView.requestFocus();
            }

            /**
             * Redraws the display, and loses the maintained information about where the drag was started from (to allow a new drag to start).
             */
            @Override
            public void mouseReleased(final MouseEvent originalEvent) {
                final MouseEvent event = zoomMouseEvent(originalEvent);

                if (event.isPopupTrigger()) {
                    popupTriggered(event);
                    return;
                }

                final Option<MouseEvent> maybeMousePressedEvent = context.mouseTracker.getLastMousePressedEvent();

                final boolean mouseDragOccurred = maybeMousePressedEvent.map(new F<MouseEvent, Boolean>() {
                    @Override
                    @NotNull
                    public Boolean f(@NotNull final MouseEvent mousePressedEvent) {
                        return !(mousePressedEvent.getX() == event.getX()) || !(mousePressedEvent.getY() == event.getY());
                    }
                }).orSome(false);

                context.mouseTracker.mouseEvent(event);

                if (startDrag != null && mouseDragOccurred) {
                    final PacketSource component = startDrag.object;

                    ComponentMoved.componentMoved(context.network, component, startDrag.index);
                }

                jiggleLayout();

                startDrag = null;
            }

            public void jiggleLayout() {
                final Dimension preferredSize = NetworkViewUtility.getPreferredSize(context);
                final JComponent networkView = context.networkView;

                networkView.setPreferredSize(preferredSize);

                final Container scrollPane = networkView.getParent().getParent();
                scrollPane.invalidate();
                scrollPane.validate();

                networkView.repaint();
            }
        };
    }
}