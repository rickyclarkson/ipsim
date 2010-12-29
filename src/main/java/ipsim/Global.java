package ipsim;

import static java.util.Collections.unmodifiableList;

import static ipsim.ContextUtility.createLogger;
import static ipsim.gui.UserPermissions.ACTUAL_TEST;
import static ipsim.gui.UserPermissions.PRACTICE_TEST_SIMULATING_ACTUAL_TEST;
import ipsim.gui.components.ProblemDialog;
import ipsim.lang.DynamicVariable;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.Problem;
import ipsim.network.ethernet.NetBlock;
import static ipsim.network.ethernet.NetMaskUtility.createNetMaskFromPrefixLength;
import static ipsim.network.ip.IPAddressUtility.valueOfUnchecked;
import ipsim.swing.CustomJOptionPane;
import ipsim.util.Collections;
import ipsim.webinterface.WebInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Logger;

public final class Global
{
	public JFrame frame=null;
	public final JTabbedPane tabbedPane=new JTabbedPane();
	public final Logger logger=createLogger();

	public final JLabel statusBar=new JLabel();
	public final JButton editProblemButton=new JButton("Edit Problem");

	private final Network ispNetwork;

	public final List<NetworkContext> networkContexts=Collections.arrayList();

	public static final DynamicVariable<Global> global=new DynamicVariable<Global>(new Global());

	public Global()
	{
		ispNetwork=new Network();
		ispNetwork.problem=new Problem(new NetBlock(valueOfUnchecked("1.0.0.0"),createNetMaskFromPrefixLength(8)),0);

		editProblemButton.setFocusable(false);

		editProblemButton.addActionListener(new ActionListener()
		{
			@Override
            public void actionPerformed(final ActionEvent e)
			{
				if (getNetworkContext().userPermissions==ACTUAL_TEST)
				{
					final String userName=getNetworkContext().emailAddress;

					final StringWriter writer=new StringWriter();

					NetworkUtility.saveToWriter(getNetworkContext().network,writer);

					try
					{
						String putSUSolution=WebInterface.putSUSolution(getNetworkContext().testNumber, userName, writer.toString());

						if ((int)'1'==(int)putSUSolution.charAt(0))
							putSUSolution="Upload completed.  You may also want to use 'Save As' to save a copy on a removable disc";

						CustomJOptionPane.showNonModalMessageDialog(frame, "Solution Uploaded", putSUSolution);
					}
					catch (final IOException exception)
					{
						NetworkContext.errors(exception.getMessage());
					}
				}
				else
					if (getNetworkContext().userPermissions==PRACTICE_TEST_SIMULATING_ACTUAL_TEST)
						NetworkContext.errors("In the real test, you will upload your solution to a central server, from where it will be marked.  You can upload as many times as you like; only the latest version will be marked.");
					else
						ProblemDialog.createProblemDialog().setVisible(true);
			}
		});
	}

	public static double zoomLevel()
	{
		return getNetworkContext().zoomLevel;
	}

	public static NetworkContext getNetworkContext()
	{
		return getNetworkContexts().get(Math.max(0, global.get().tabbedPane.getSelectedIndex()));
	}

	public static List<NetworkContext> getNetworkContexts()
	{
		return global.get().networkContexts;
	}

	public static Network getIspNetwork()
	{
		return global.get().ispNetwork;
	}
}