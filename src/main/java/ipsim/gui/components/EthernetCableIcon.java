package ipsim.gui.components;

import fj.F;
import fj.data.Option;
import ipsim.Global;
import ipsim.awt.Point;
import ipsim.gui.PositionUtility;
import ipsim.lang.Assertion;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;
import org.jetbrains.annotations.NotNull;

import static ipsim.Global.getNetworkContext;

public final class EthernetCableIcon
{
	public static JToggleButton newButton()
	{
		final JToggleButton button=new JToggleButton("Ethernet Cable", EthernetCableHandler.icon);

		final MouseInputAdapter listener=new MouseInputAdapter()
		{
			public Option<Point> startDrag=Option.none();

			public MouseEvent zoomMouseEvent(final MouseEvent event)
			{
				return new MouseEvent(event.getComponent(), event.getID(), event.getWhen(), event.getModifiers(), (int)((double)event.getX()/Global.zoomLevel()), (int)((double)event.getY()/Global.zoomLevel()), event.getClickCount(), event.isPopupTrigger(), event.getButton());
			}

			/**
			 * Keeps track of where the mouse drag was started from, and renders the
			 * object that was closest to the starting position, giving the
			 * impression that the object (or part of it) is being dragged across
			 * the display.
			 */
			@Override
			public void mouseDragged(final MouseEvent originalEvent)
			{
				super.mouseDragged(originalEvent);

				final MouseEvent event=zoomMouseEvent(originalEvent);

				getNetworkContext().mouseTracker.mouseEvent(event);

				final JComponent view=getNetworkContext().networkView;

				view.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

				if (!startDrag.isSome())
					startDrag=Option.some(new Point((double)event.getX(), (double)event.getY()));

				final Rectangle visibleRect=view.getVisibleRect();

				int changedX=0;
				if (originalEvent.getX()>=visibleRect.x+visibleRect.width-10)
				{
					changedX=1;
					visibleRect.x+=10;
				}

				int changedY=0;
				if (originalEvent.getY()>=visibleRect.y+visibleRect.height-10)
				{
					changedY=1;
					visibleRect.y+=10;
				}

				if (originalEvent.getX()<=visibleRect.x+10)
				{
					changedX=-1;
					visibleRect.x-=10;
				}

				if (originalEvent.getY()<=visibleRect.y+10)
				{
					changedY=-1;
					visibleRect.y-=10;
				}

				if (!(0==changedX) || !(0==changedY))
				{
					PositionUtility.translateAllWhenNecessary(getNetworkContext(), visibleRect);

					view.scrollRectToVisible(visibleRect);
				}

				view.invalidate();
				view.validate();
				view.repaint();
			}

			@Override
			public void mousePressed(final MouseEvent originalEvent)
			{
				super.mousePressed(originalEvent);
				final MouseEvent event=zoomMouseEvent(originalEvent);

				getNetworkContext().mouseTracker.mouseEvent(event);

				final Point point=new Point((double)event.getX(), (double)event.getY());

				if (event.getButton()==MouseEvent.BUTTON1)
					startDrag=Option.some(point);
			}

			/**
			 * Redraws the display, and loses the maintained information about where
			 * the drag was started from (to allow a new drag to start).
			 */
			@Override
			public void mouseReleased(final MouseEvent originalEvent)
			{
				super.mouseReleased(originalEvent);

				final MouseEvent event=zoomMouseEvent(originalEvent);

				final Option<MouseEvent> maybeMousePressedEvent=getNetworkContext().mouseTracker.getLastMousePressedEvent();

				final boolean mouseDragOccurred=maybeMousePressedEvent.map(new F<MouseEvent, Boolean>()
				{
					@Override
                    @NotNull
					public Boolean f(@NotNull final MouseEvent mousePressedEvent)
					{
						return !(mousePressedEvent.getX()==event.getX()) || !(mousePressedEvent.getY()==event.getY());
					}
				}).orSome(false);

				getNetworkContext().mouseTracker.mouseEvent(event);

				if (mouseDragOccurred)
					for (Point point: startDrag) {
                        getNetworkContext().network.modified=true;

                        final PacketSource cable=NetworkComponentUtility.create(getNetworkContext(), Cable.class, point, new Point((double)event.getX(), (double)event.getY()));

                        Assertion.assertNotNull(PositionUtility.getPosition(getNetworkContext().network,cable,1));

                        button.doClick();

                        ComponentMoved.componentMoved(getNetworkContext().network, cable, 0, 1);

                        getNetworkContext().networkView.invalidate();
                        getNetworkContext().networkView.validate();
                        getNetworkContext().networkView.repaint();
					}

				startDrag=Option.none();
			}
		};

		button.setVerticalAlignment(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setHorizontalTextPosition(SwingConstants.CENTER);

		button.addActionListener(new ActionListener()
		{
			@Override
            public void actionPerformed(final ActionEvent e)
			{
				final Component view=Global.getNetworkContext().networkView;

				if (button.isSelected())
				{
					Global.getNetworkContext().toggleListeners.off();

					view.addMouseListener(listener);
					view.addMouseMotionListener(listener);

					view.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
				else
				{
					view.removeMouseListener(listener);
					view.removeMouseMotionListener(listener);

					Global.getNetworkContext().toggleListeners.on();

					view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});

		button.setToolTipText("Click on this to draw an Ethernet Cable, then drag on the display to make one appear");
		return button;
	}
}