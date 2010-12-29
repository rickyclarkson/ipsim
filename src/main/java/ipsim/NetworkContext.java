package ipsim;

import fj.data.Option;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import fpeas.sideeffect.SideEffect;
import ipsim.gui.NetworkView;
import ipsim.gui.NetworkViewUtility;
import ipsim.gui.Toggle;
import ipsim.gui.UserPermissions;
import ipsim.gui.event.MouseTracker;
import ipsim.network.Network;
import ipsim.network.Problem;
import ipsim.property.Property;
import ipsim.swing.CustomJOptionPane;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import static fpeas.maybe.MaybeUtility.nothing;
import static ipsim.Global.global;
import static ipsim.property.PropertyUtility.newProperty;

public final class NetworkContext
{
	public double zoomLevel=1.0;
	public String emailAddress=null;
	public final NetworkView networkView=NetworkViewUtility.newNetworkView(this);
	public final JFileChooser fileChooser;
	public final Property<Option<File>> currentFilename=newProperty(Option.<File>none());
	public Network network=new Network();

	public final MouseTracker mouseTracker=new MouseTracker()
	{
		public Maybe<Integer> x=nothing();
		public Maybe<Integer> y=nothing();
		public Maybe<MouseEvent> lastMousePressedEvent=nothing();

		@Override
        public void mouseEvent(final MouseEvent event)
		{
			x=MaybeUtility.just(event.getX());
			y=MaybeUtility.just(event.getY());

			if (MouseEvent.MOUSE_PRESSED==event.getID())
				lastMousePressedEvent=MaybeUtility.just(event);
		}

		@Override
        public Maybe<Integer> getX()
		{
			return x;
		}

		@Override
        public Maybe<Integer> getY()
		{
			return y;
		}

		@Override
        public Maybe<MouseEvent> getLastMousePressedEvent()
		{
			return lastMousePressedEvent;
		}
	};

	public final SideEffect<String> errors=new SideEffect<String>()
	{
		@Override
        public void run(final String s)
		{
			errors(s);
		}
	};

	public final Toggle toggleListeners=new Toggle()
	{
		public MouseListener[] mouseListeners;
		public MouseMotionListener[] motionListeners;

		@Override
        public void off()
		{
			mouseListeners=networkView.getMouseListeners();

			motionListeners=networkView.getMouseMotionListeners();

			int a;

			for (a=0;a<mouseListeners.length;a++)
				networkView.removeMouseListener(mouseListeners[a]);

			for (a=0;a<motionListeners.length;a++)
				networkView.removeMouseMotionListener(motionListeners[a]);
		}

		@Override
        public void on()
		{
			int a;

			for (a=0;a<mouseListeners.length;a++)
				networkView.addMouseListener(mouseListeners[a]);

			for (a=0;a<motionListeners.length;a++)
				networkView.addMouseMotionListener(motionListeners[a]);
		}
	};

	public static boolean confirm(final String message)
	{
		return JOptionPane.YES_OPTION==CustomJOptionPane.showYesNoCancelDialog(global.get().frame, message, "Confirm");
	}

	public static void errors(final String s)
	{
		JOptionPane.showMessageDialog(global.get().frame, s, "Error", JOptionPane.ERROR_MESSAGE);
	}

	//1 to 5
	public static int askUserForNumberOfFaults()
	{
		while (true)
			try
			{
				return Integer.valueOf(JOptionPane.showInputDialog(global.get().frame, "How many faults? (1 to 5)"));
			}
			catch (NumberFormatException e)
			{
			}
	}

	public static void whenProblemChanges(final Maybe<Problem> maybe)
	{

	}

	public UserPermissions userPermissions=UserPermissions.FREEFORM;

	public String testNumber=null;

	public NetworkContext(final JFileChooser fileChooser)
	{
		this.fileChooser=fileChooser;
	}
}