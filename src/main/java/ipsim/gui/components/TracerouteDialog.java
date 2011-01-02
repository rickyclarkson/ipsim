package ipsim.gui.components;

import anylayout.AnyLayout;
import anylayout.extras.PercentConstraints;
import anylayout.extras.PercentConstraintsUtility;
import anylayout.extras.SizeCalculatorUtility;
import fj.Effect;
import ipsim.Global;
import ipsim.NetworkContext;
import ipsim.gui.event.CommandUtility;
import ipsim.io.IOUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.traceroute.Traceroute;
import ipsim.network.connectivity.traceroute.TracerouteResults;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;
import ipsim.swing.IPAddressTextField;
import ipsim.swing.IPAddressTextFieldUtility;
import ipsim.util.Collections;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import static anylayout.extras.ConstraintUtility.typicalDefaultConstraint;
import static ipsim.Global.getNetworkContext;
import static ipsim.lang.Runnables.throwRuntimeException;
import static ipsim.swing.Buttons.closeButton;
import static ipsim.swing.Dialogs.createDialogWithEscapeKeyToClose;

public final class TracerouteDialog {
    public static JDialog newTracerouteDialog(final Computer computer) {
        final JDialog dialog = createDialogWithEscapeKeyToClose(Global.global.get().frame, "Traceroute");

        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        dialog.setLocation(200, 100);
        dialog.setSize(400, 400);

        final Container pane = dialog.getContentPane();

        final PercentConstraints constraints = PercentConstraintsUtility.newInstance(pane);
        AnyLayout.useAnyLayout(pane, 0.5f, 0.5f, SizeCalculatorUtility.absoluteSize(200, 100), typicalDefaultConstraint(throwRuntimeException));

        constraints.add(new JLabel("IP Address"), 10, 5, 25, 5, false, false);

        final IPAddressTextField ipAddressTextField = IPAddressTextFieldUtility.newInstance();

        constraints.add(ipAddressTextField.textField, 30, 5, 25, 5, false, false);

        final JButton button = new JButton("Traceroute");

        constraints.add(button, 60, 5, 30, 5, false, false);

        constraints.add(new JLabel("Output:"), 5, 15, 30, 5, false, false);

        final JTextArea outputArea = new JTextArea(5, 5);

        final JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(outputArea);

        constraints.add(new JScrollPane(outputPanel), 10, 25, 80, 65, true, true);

        final JButton closeButton = closeButton("Close", dialog);

        constraints.add(closeButton, 70, 90, 20, 10, false, false);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                outputArea.setText("");

                if (0 == ipAddressTextField.textField.getText().length()) {
                    NetworkContext.errors("Cannot traceroute without an IP address");

                    return;
                }

                final IPAddress address;
                try {
                    address = IPAddressUtility.valueOf(ipAddressTextField.textField.getText());
                } catch (final CheckedNumberFormatException exception) {
                    NetworkContext.errors(exception.getMessage());
                    return;
                }

                final TracerouteResults results = Traceroute.trace(getNetworkContext().network, computer, new DestIPAddress(address), 30);

                IOUtility.withPrintWriter(DocumentWriter.documentWriter(outputArea.getDocument()), new Effect<PrintWriter>() {
                    @Override
                    public void e(final PrintWriter printWriter) {
                        printWriter.println(results.asString());
                    }
                });

                final Network network = getNetworkContext().network;
                network.log = Collections.add(network.log, CommandUtility.traceroute(computer, address, results.size(), getNetworkContext().network));
            }
        });

        return dialog;
    }
}