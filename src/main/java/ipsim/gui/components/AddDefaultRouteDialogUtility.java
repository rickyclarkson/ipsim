package ipsim.gui.components;

import anylayout.extras.ConstraintUtility;
import anylayout.extras.PercentConstraints;
import anylayout.extras.PercentConstraintsUtility;
import fj.data.Option;
import fpeas.sideeffect.SideEffectUtility;
import ipsim.Global;
import ipsim.awt.ComponentUtility;
import ipsim.lang.Runnables;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetBlockUtility;
import ipsim.swing.IPAddressTextField;
import ipsim.swing.IPAddressTextFieldUtility;
import ipsim.util.Collections;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import static anylayout.AnyLayout.useAnyLayout;
import static ipsim.NetworkContext.errors;
import static ipsim.gui.event.CommandUtility.addDefaultRoute;
import static ipsim.network.ethernet.ComputerUtility.isLocallyReachable;
import static ipsim.swing.Buttons.closeButton;
import static ipsim.swing.Dialogs.createDialogWithEscapeKeyToClose;

public class AddDefaultRouteDialogUtility
{
	public static JDialog newInstance(final Computer computer)
	{
		final JDialog dialog=createDialogWithEscapeKeyToClose(Global.global.get().frame, "Add a Default Route");

		dialog.setSize(400, 200);

		ComponentUtility.centreOnParent(dialog, Global.global.get().frame);

		final Container pane=dialog.getContentPane();

		final PercentConstraints constraints=PercentConstraintsUtility.newInstance(pane);
		useAnyLayout(pane, 0.5f, 0.5f, constraints.getSizeCalculator(), ConstraintUtility.typicalDefaultConstraint(Runnables.throwRuntimeException));

		constraints.add(new JLabel("IP Address"), 10, 10, 25, 15, false, false);

		final IPAddressTextField ipAddressTextField=IPAddressTextFieldUtility.newInstance();

		constraints.add(ipAddressTextField.textField, 40, 10, 25, 15, false, false);
		final JButton okButton=new JButton("OK");
		constraints.add(okButton, 10, 70, 15, 15, false, false);

		okButton.addActionListener(new ActionListener()
		{
			@Override
            public void actionPerformed(final ActionEvent event)
			{
				final NetBlock zero=NetBlockUtility.getZero();

				final IPAddress ipAddress=ipAddressTextField.getIPAddress();

				if (isLocallyReachable(computer, ipAddress))
				{
					final Route route=new Route(zero, ipAddress);

					computer.routingTable.add(Option.some(computer), route, SideEffectUtility.<IPAddress>throwRuntimeException());

					final Network network=Global.getNetworkContext().network;
					network.log=Collections.add(network.log,addDefaultRoute(computer, ipAddress, network));

					network.modified=true;

					dialog.setVisible(false);
					dialog.dispose();
				}
				else
				{
					errors("Gateway unreachable");
					dialog.requestFocus();
				}
			}
		});

		final JButton cancelButton=closeButton("Cancel", dialog);
		constraints.add(cancelButton, 70, 70, 25, 15, false, false);

		return dialog;
	}
}