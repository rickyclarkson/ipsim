package ipsim.gui.components;

import anylayout.AnyLayout;
import anylayout.extras.ConstraintUtility;
import anylayout.extras.PercentConstraints;
import anylayout.extras.PercentConstraintsUtility;
import anylayout.extras.SizeCalculatorUtility;
import ipsim.Global;
import ipsim.Globals;
import ipsim.awt.ComponentUtility;
import ipsim.gui.event.CommandUtility;
import ipsim.lang.Runnables;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ping.PingResults;
import ipsim.network.connectivity.ping.Pinger;
import ipsim.swing.Buttons;
import ipsim.swing.Dialogs;
import ipsim.swing.IPAddressTextField;
import ipsim.swing.IPAddressTextFieldUtility;
import ipsim.util.Collections;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PingDialog {
    public final JDialog jDialog;

    public PingDialog(final Computer computer) {
        jDialog = Dialogs.createDialogWithEscapeKeyToClose(Global.global.get().frame, "Ping");

        final PercentConstraints constraints = PercentConstraintsUtility.newInstance(jDialog.getContentPane());

        jDialog.setSize(600, 400);
        ComponentUtility.centreOnParent(jDialog, Global.global.get().frame);

        AnyLayout.useAnyLayout(jDialog.getContentPane(), 0.5f, 0.5f, SizeCalculatorUtility.absoluteSize(600, 400), ConstraintUtility.typicalDefaultConstraint(Runnables.throwRuntimeException));

        constraints.add(new JLabel("IP Address"), 5, 5, 25, 10, true, true);

        final IPAddressTextField ipAddressTextField = IPAddressTextFieldUtility.newInstance();

        ipAddressTextField.setIPAddress(new IPAddress(0));

        constraints.add(ipAddressTextField.textField, 5, 15, 25, 10, false, false);

        final JButton pingButton = new JButton("Ping");
        pingButton.setMnemonic('P');

        constraints.add(pingButton, 35, 15, 25, 10, false, false);

        final JTextArea textArea = new JTextArea(10, 10);
        textArea.setEditable(false);

        pingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                final IPAddress ipAddress = ipAddressTextField.getIPAddress();

                final Writer documentWriter = DocumentWriter.documentWriter(textArea.getDocument());

                final PrintWriter printWriter = new PrintWriter(documentWriter);

                final List<PingResults> pingResults = Pinger.ping(Global.getNetworkContext().network, computer, new DestIPAddress(ipAddress), Globals.DEFAULT_TIME_TO_LIVE);

                final Network network = Global.getNetworkContext().network;
                network.log = Collections.add(network.log, CommandUtility.ping(computer, ipAddress, pingResults, Global.getNetworkContext().network));

                try {
                    printWriter.println(Collections.<PingResults>asString(pingResults, "\n"));
                } finally {
                    printWriter.close();
                }
            }

        });

        constraints.add(new JScrollPane(textArea), 10, 30, 80, 60, true, true);

        final JButton closeButton = Buttons.closeButton("Close", jDialog);
        closeButton.setMnemonic('C');

        constraints.add(closeButton, 75, 15, 25, 10, false, false);
    }
}