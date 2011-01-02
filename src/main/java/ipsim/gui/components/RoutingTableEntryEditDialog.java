package ipsim.gui.components;

import anylayout.extras.PercentConstraints;
import anylayout.extras.PercentConstraintsUtility;
import anylayout.extras.SizeCalculatorUtility;
import fj.Effect;
import fj.data.Option;
import ipsim.Global;
import ipsim.gui.event.CommandUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetBlock;
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
import static ipsim.lang.Runnables.throwRuntimeException;
import static ipsim.swing.Buttons.closeButton;
import static ipsim.swing.Dialogs.createDialogWithEscapeKeyToClose;

public final class RoutingTableEntryEditDialog {
    public static RouteEditDialog createRoutingTableEntryEditDialog(final Computer computer, final RouteInfo entry, final Option<Route> maybeRealRoute, final Option<RoutingTableDialog> maybeParent) {
        final JDialog dialog = createDialogWithEscapeKeyToClose(Global.global.get().frame, "Edit Route");

        dialog.setSize(400, 200);
        dialog.setTitle("Edit Route");

        return createRouteEditor(dialog, Global.getNetworkContext().network, computer, entry, maybeRealRoute, maybeParent);
    }

    public static RouteEditDialog createRouteEditor(final JDialog dialog, final Network network, final Computer computer, final RouteInfo entry, final Option<Route> maybeRealRoute, final Option<RoutingTableDialog> maybeParent) {
        final PercentConstraints constraints = PercentConstraintsUtility.newInstance(dialog.getContentPane());
        useAnyLayout(dialog.getContentPane(), 0.5f, 0.5f, SizeCalculatorUtility.absoluteSize(400, 200), typicalDefaultConstraint(throwRuntimeException));

        constraints.add(new JLabel("Destination Network"), 5, 5, 45, 10, false, false);

        final IPAddressTextField networkNumberTextField = IPAddressTextFieldUtility.newInstance();

        networkNumberTextField.setIPAddress(entry.destination.networkNumber);

        constraints.add(networkNumberTextField.textField, 50, 5, 45, 10, false, false);

        constraints.add(new JLabel("Destination Subnet Mask"), 5, 20, 45, 10, false, false);

        final SubnetMaskTextField subnetMaskTextField = new SubnetMaskTextField();

        subnetMaskTextField.setNetMask(entry.destination.netMask);

        constraints.add(subnetMaskTextField, 50, 20, 30, 10, false, false);
        constraints.add(new JLabel("Gateway"), 5, 40, 45, 10, false, false);

        final IPAddressTextField ipAddress = IPAddressTextFieldUtility.newInstance();

        ipAddress.setIPAddress(entry.gateway);
        constraints.add(ipAddress.textField, 50, 40, 30, 10, false, false);

        final JButton okButton = new JButton("OK");

        final RouteInfo entry1 = new RouteInfo(entry.destination, entry.gateway);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                final IPAddress networkNumber = networkNumberTextField.getIPAddress();

                final NetMask netMask = subnetMaskTextField.getNetMask();

                final RouteInfo newEntry = new RouteInfo(new NetBlock(networkNumber, netMask), ipAddress.getIPAddress());

                final Route realEntry = new Route(newEntry.destination, newEntry.gateway);

                if (maybeRealRoute.isSome()) {
                    Route route = maybeRealRoute.some();
                    final RouteInfo previous = new RouteInfo(route.block, route.gateway);

                    computer.routingTable.replace(route, new Route(newEntry.destination, newEntry.gateway));
                    network.log = Collections.add(network.log, CommandUtility.changedRoute(computer, newEntry, previous, network));

                } else {
                    computer.routingTable.add(Option.some(computer), realEntry, Effect.<IPAddress>throwRuntimeException());
                    network.log = Collections.add(network.log, CommandUtility.addExplicitRoute(computer, entry1.destination, entry1.gateway, network));
                }

                for (RoutingTableDialog parent : maybeParent)
                    RoutingTableDialogUtility.populateElements.e(parent);

                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        constraints.add(okButton, 20, 80, 20, 15, false, false);
        constraints.add(closeButton("Cancel", dialog), 60, 80, 20, 15, false, false);

        return new RouteEditDialog() {
            @Override
            public JDialog getDialog() {
                return dialog;
            }

        };
    }
}