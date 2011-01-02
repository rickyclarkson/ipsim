package ipsim.gui.components;

import anylayout.extras.PercentConstraints;
import anylayout.extras.PercentConstraintsUtility;
import ipsim.Global;
import ipsim.awt.ComponentUtility;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.gui.event.CommandUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.swing.Dialogs;
import ipsim.swing.IPAddressTextField;
import ipsim.swing.IPAddressTextFieldUtility;
import ipsim.swing.SubnetMaskTextField;
import ipsim.util.Collections;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import static anylayout.AnyLayout.useAnyLayout;
import static anylayout.extras.ConstraintUtility.typicalDefaultConstraint;
import static ipsim.NetworkContext.errors;
import static ipsim.lang.Runnables.throwRuntimeException;
import static ipsim.network.ethernet.ComputerUtility.getEth;
import static ipsim.swing.Buttons.closeButton;

public class EditIPAddressDialogFactory {
    public static JDialog newInstance(final Computer computer, final int ethNo) {
        final JDialog dialog = Dialogs.createDialogWithEscapeKeyToClose(Global.global.get().frame, "Edit IP Address");

        dialog.setSize(400, 200);

        ComponentUtility.centreOnParent(dialog, Global.global.get().frame);

        final PercentConstraints constraints = PercentConstraintsUtility.newInstance(dialog.getContentPane());
        useAnyLayout(dialog.getContentPane(), 0.5f, 0.5f, constraints.getSizeCalculator(), typicalDefaultConstraint(throwRuntimeException));

        constraints.add(new JLabel("IP Address"), 10, 5, 25, 15, false, false);

        final IPAddressTextField ipAddressTextField = IPAddressTextFieldUtility.newInstance();

        final CardDrivers card = getEth(computer, ethNo);

        ipAddressTextField.setIPAddress(card.ipAddress.get());

        constraints.add(ipAddressTextField.textField, 40, 5, 25, 15, false, false);

        constraints.add(new JLabel("Subnet Mask"), 10, 45, 25, 15, false, false);

        final SubnetMaskTextField subnetMaskTextField = new SubnetMaskTextField();

        final CardDrivers card2 = getEth(computer, ethNo);

        subnetMaskTextField.setNetMask(card2.netMask.get());

        constraints.add(subnetMaskTextField, 40, 45, 25, 15, false, false);

        final JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final CardDrivers cardWithDrivers = getEth(computer, ethNo);

                final IPAddress before = cardWithDrivers.ipAddress.get();
                final NetMask beforeNetMask = cardWithDrivers.netMask.get();

                final String cardBefore = PacketSourceUtility.asString(Global.getNetworkContext().network, cardWithDrivers.card);

                cardWithDrivers.ipAddress.set(ipAddressTextField.getIPAddress());

                try {
                    cardWithDrivers.netMask.set(NetMaskUtility.valueOf(subnetMaskTextField.getText()));
                } catch (final CheckedNumberFormatException exception) {
                    errors(exception.getMessage());
                    return;
                }

                final Network network = Global.getNetworkContext().network;
                network.log = Collections.add(network.log, CommandUtility.ipChange(cardWithDrivers, before, beforeNetMask, cardBefore, Global.getNetworkContext().network));

                Global.getNetworkContext().network.modified = true;
                dialog.setVisible(false);
                dialog.dispose();

            }
        });

        constraints.add(okButton, 10, 80, 25, 15, false, false);

        constraints.add(closeButton("Cancel", dialog), 60, 80, 25, 15, false, false);

        return dialog;

    }
}