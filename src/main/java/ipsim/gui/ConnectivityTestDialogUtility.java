package ipsim.gui;

import anylayout.AnyLayout;
import anylayout.extras.PercentConstraints;
import anylayout.extras.PercentConstraintsUtility;
import static anylayout.extras.SizeCalculatorUtility.absoluteSize;
import fpeas.function.Function;
import fpeas.sideeffect.SideEffect;
import ipsim.Global;
import ipsim.awt.ComponentUtility;
import ipsim.gui.event.CommandUtility;
import ipsim.lang.StringUtility;
import ipsim.network.connectivity.ConnectivityResults;
import ipsim.network.connectivity.ConnectivityTest;
import ipsim.network.connectivity.hub.ProgressMonitor;
import ipsim.network.Network;
import static ipsim.swing.Buttons.closeButton;
import static ipsim.swing.Dialogs.createDialogWithEscapeKeyToClose;
import ipsim.swing.SwingWorker;
import ipsim.util.Collections;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import static javax.swing.SwingUtilities.invokeLater;
import java.awt.*;

/**
 * Dialog that shows the results of a connectivity test (menu item Operations-&gt;Test Connectivity).
 */
public final class ConnectivityTestDialogUtility
{
	public static void createConnectivityTestDialog(final SideEffect<JDialog> doOnCompletion)
	{
		final JFrame realFrame=Global.global.get().frame;
		final JDialog dialog=createDialogWithEscapeKeyToClose(realFrame, "Connectivity Test");

		dialog.setSize(700, 400);
		AnyLayout.useAnyLayout(dialog.getContentPane(), 0.5f, 0.5f, absoluteSize(700, 400), null);
		final PercentConstraints constraints=PercentConstraintsUtility.newInstance(dialog.getContentPane());

		ComponentUtility.centreOnParent(dialog, realFrame);

		final JLabel resultsLabel=new JLabel();
		constraints.add(resultsLabel, 5, 5, 90, 5, false, false);

		constraints.add(new JLabel("Connectivity Problems:"), 5, 15, 90, 5, false, false);

		final JTextArea problemList=new JTextArea();
		constraints.add(new JScrollPane(problemList), 5, 25, 90, 60, true, true);

		constraints.add(closeButton("Close", dialog), 80, 90, 15, 6, true, true);

		final Cursor original=realFrame.getCursor();

		realFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		final ProgressMonitor monitor=new ProgressMonitor(realFrame, "Testing connectivity                  ", "Testing connectivity                    ", 0, 100);

		monitor.setMillisToPopup(0);
		monitor.setMillisToDecideToPopup(0);
		monitor.setProgress(1);
		new SwingWorker<Void, Object>()
		{
			@Override
			public Void doInBackground()
			{
				try
				{
					final Network network=Global.getNetworkContext().network;
					final ConnectivityResults results=ConnectivityTest.testConnectivity(network,ProgressMonitorUtility.setNote(monitor),ProgressMonitorUtility.setProgress(monitor));
					problemList.setText(StringUtility.join(results.getOutputs(), "\n"));

					resultsLabel.setText(results.getPercentConnected()+"% connected.");

					network.log=Collections.add(network.log,CommandUtility.connectivityTested(resultsLabel.getText()));

					problemList.setText(Collections.append(results.getOutputs(), new Function<String, String>()
					{
						@Override
                        @NotNull
						public String run(@NotNull final String item)
						{
							return item+'\n';
						}
					}));

					invokeLater(new Runnable()
					{
						@Override
                        public void run()
						{
							monitor.setProgress(100);
							monitor.close();
							dialog.setVisible(true);
							doOnCompletion.run(dialog);
						}
					});
				}
				catch (final RuntimeException exception)
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
                        public void run()
						{
							throw exception;
						}
					});
				}
				finally
				{
					realFrame.setCursor(original);
				}

				return null;
			}
		}.execute();
	}
}