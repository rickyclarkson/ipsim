package ipsim.gui.event;

import fj.data.Option;
import java.awt.event.MouseEvent;

public interface MouseTracker
{
	void mouseEvent(MouseEvent event);

	Option<Integer> getX();

	Option<Integer> getY();

	Option<MouseEvent> getLastMousePressedEvent();
}
