package ipsim;

import fj.Effect;
import fj.data.Option;
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

import static fj.data.Option.none;
import static fj.data.Option.some;
import static ipsim.Global.global;
import static ipsim.property.PropertyUtility.newProperty;

public final class NetworkContext {
    public double zoomLevel = 1.0;
    public String emailAddress = null;
    public final NetworkView networkView = NetworkViewUtility.newNetworkView(this);
    public final JFileChooser fileChooser;
    public final Property<Option<File>> currentFilename = newProperty(Option.<File>none());
    public Network network = new Network();

    public final MouseTracker mouseTracker = new MouseTracker() {
        public Option<Integer> x = none();
        public Option<Integer> y = none();
        public Option<MouseEvent> lastMousePressedEvent = none();

        @Override
        public void mouseEvent(final MouseEvent event) {
            x = some(event.getX());
            y = some(event.getY());

            if (MouseEvent.MOUSE_PRESSED == event.getID())
                lastMousePressedEvent = some(event);
        }

        @Override
        public Option<Integer> getX() {
            return x;
        }

        @Override
        public Option<Integer> getY() {
            return y;
        }

        @Override
        public Option<MouseEvent> getLastMousePressedEvent() {
            return lastMousePressedEvent;
        }
    };

    public final Effect<String> errors = new Effect<String>() {
        @Override
        public void e(final String s) {
            errors(s);
        }
    };

    public final Toggle toggleListeners = new Toggle() {
        public MouseListener[] mouseListeners;
        public MouseMotionListener[] motionListeners;

        @Override
        public void off() {
            mouseListeners = networkView.getMouseListeners();

            motionListeners = networkView.getMouseMotionListeners();

            int a;

            for (a = 0; a < mouseListeners.length; a++)
                networkView.removeMouseListener(mouseListeners[a]);

            for (a = 0; a < motionListeners.length; a++)
                networkView.removeMouseMotionListener(motionListeners[a]);
        }

        @Override
        public void on() {
            int a;

            for (a = 0; a < mouseListeners.length; a++)
                networkView.addMouseListener(mouseListeners[a]);

            for (a = 0; a < motionListeners.length; a++)
                networkView.addMouseMotionListener(motionListeners[a]);
        }
    };

    public static boolean confirm(final String message) {
        return JOptionPane.YES_OPTION == CustomJOptionPane.showYesNoCancelDialog(global.get().frame, message, "Confirm");
    }

    public static void errors(final String s) {
        JOptionPane.showMessageDialog(global.get().frame, s, "Error", JOptionPane.ERROR_MESSAGE);
    }

    //1 to 5
    public static int askUserForNumberOfFaults() {
        while (true)
            try {
                return Integer.valueOf(JOptionPane.showInputDialog(global.get().frame, "How many faults? (1 to 5)"));
            } catch (NumberFormatException ignored) {
            }
    }

    public static void whenProblemChanges(final Option<Problem> maybe) {

    }

    public UserPermissions userPermissions = UserPermissions.FREEFORM;

    public String testNumber = null;

    public NetworkContext(final JFileChooser fileChooser) {
        this.fileChooser = fileChooser;
    }
}