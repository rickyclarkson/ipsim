package ipsim.gui.event;

import fpeas.maybe.Maybe;

import java.awt.event.MouseEvent;

public interface MouseTracker
{
	void mouseEvent(MouseEvent event);

	Maybe<Integer> getX();

	Maybe<Integer> getY();

	Maybe<MouseEvent> getLastMousePressedEvent();
}
