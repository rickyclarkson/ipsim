package ipsim.gui;

import anylayout.AnyLayout;
import anylayout.SizeCalculator;
import anylayout.extras.ConstraintUtility;
import anylayout.extras.RelativeConstraints;
import fj.F;
import fj.Function;
import fj.data.Option;
import fpeas.either.EitherUtility;
import fpeas.lazy.Lazy;
import fpeas.maybe.MaybeUtility;
import fpeas.predicate.Predicate;
import fpeas.sideeffect.SideEffect;
import fpeas.sideeffect.SideEffectUtility;
import ipsim.Caster;
import ipsim.Global;
import ipsim.Globals;
import ipsim.NetworkContext;
import ipsim.awt.Point;
import ipsim.gui.components.initialdialog.InitialDialogUtility;
import ipsim.gui.event.LogUtility;
import ipsim.lang.Assertion;
import ipsim.lang.CheckedIllegalStateException;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.Problem;
import ipsim.network.ProblemBuilder;
import ipsim.network.ProblemDifficulty;
import ipsim.network.ProblemUtility;
import ipsim.network.breakage.BreakNetwork;
import ipsim.network.conformance.ConformanceTests;
import ipsim.network.connectivity.ConnectivityResults;
import ipsim.network.connectivity.ConnectivityTest;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.ComputerFactory;
import ipsim.network.connectivity.hub.ProgressMonitor;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.property.Property;
import ipsim.property.PropertyListener;
import ipsim.swing.CustomJOptionPane;
import ipsim.swing.CustomJOptionPaneResult;
import ipsim.swing.JFileChooserUtility;
import ipsim.util.Arrays;
import ipsim.util.Collections;
import ipsim.webinterface.NamedConfiguration;
import ipsim.webinterface.NoSuchConfigurationException;
import ipsim.webinterface.WebInterface;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.jetbrains.annotations.NotNull;

import static fj.data.Option.some;
import static ipsim.Global.getNetworkContext;
import static ipsim.Global.global;
import static ipsim.NetworkContext.errors;
import static ipsim.lang.Assertion.assertNotNull;
import static ipsim.network.NetworkUtility.loadFromFile;
import static java.lang.Integer.parseInt;

public class MenuHandler
{
	/**
	 * Called when the File-&gt;New menu item is selected.
	 */
	public static Runnable networkNew()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				if (!Collections.all(Global.getNetworkContexts(),new Predicate<NetworkContext>()
				{
					@Override
                    public boolean invoke(final NetworkContext networkContext)
					{
						return networkContext.userPermissions.allowMultipleNetworks();
					}
				},true))
					return;

				final NetworkContext networkContext=new NetworkContext(new JFileChooser());

				final Component scrollPane=new JScrollPane(networkContext.networkView);
				final JTabbedPane tabbedPane=global.get().tabbedPane;
				tabbedPane.addTab("Untitled", scrollPane);
				tabbedPane.setSelectedComponent(scrollPane);
				final int selected=tabbedPane.getSelectedIndex();

				networkContext.currentFilename.addPropertyListener(new PropertyListener<Option<File>>()
				{
					@Override
                    public void propertyChanged(final Property<Option<File>> property, final Option<File> oldValue, final Option<File> newValue)
					{
						Assertion.assertNotNull(newValue);
						tabbedPane.setTitleAt(selected, newValue.map(new F<File, String>()
						{
							@Override
                            @NotNull
							public String f(@NotNull final File file)
							{
								return file.getName();
							}
						}).orSome("Untitled"));
					}
				});

				Global.getNetworkContexts().add(networkContext);

				getNetworkContext().network.problem=null;
				getNetworkContext().currentFilename.set(Option.<File>none());
				global.get().editProblemButton.setText("Edit Problem");

				InitialDialogUtility.createInitialDialog().dialog.setVisible(true);

				global.get().frame.repaint();
			}
		};
	}

	/**
	 * Called when the File-&gt;Open menu item is selected.
	 */
	public static Runnable fileOpen()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				if (getNetworkContext().network.modified && !networkModifiedDialog())
					return;

				final int result=getNetworkContext().fileChooser.showOpenDialog(global.get().frame);

				if (result==JFileChooser.APPROVE_OPTION)
				{
					final File filename=getNetworkContext().fileChooser.getSelectedFile();

					loadFromFile(getNetworkContext().network, filename, new SideEffect<IOException>()
					{
						@Override
                        public void run(final IOException input)
						{
							JOptionPane.showMessageDialog(global.get().frame, filename+" is not a valid IPSim datafile ("+input.getMessage()+')', "Error", JOptionPane.ERROR_MESSAGE);
						}
					});

					global.get().frame.repaint();
				}

			}
		};
	}

	public static Runnable fileSave()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
                final Option<File> fileOption = getNetworkContext().currentFilename.get();
                if (fileOption.isSome())
                    try
                    {
                        NetworkUtility.saveToFile(getNetworkContext().network, fileOption.some());
                    }
                    catch (IOException e)
                    {
                        errors(e.getMessage());
                    }
				else
                    fileSaveAs().run();
			}
		};
	}

	/**
	 * Called when the File-&gt;Save As menu item is selected. Displays a save dialog then saves the file to the specified filename.
	 */
	public static Runnable fileSaveAs()
	{
		final SideEffect<JFrame> sideEffect=new SideEffect<JFrame>()
		{
			@Override
            public void run(final JFrame realFrame)
			{
				for (;;)
				{
					final int result=getNetworkContext().fileChooser.showSaveDialog(realFrame);

					if (result==JFileChooser.APPROVE_OPTION)
					{
						final File filename=getNetworkContext().fileChooser.getSelectedFile();

						if (filename.exists())
						{
							final int optResult=CustomJOptionPane.showYesNoCancelDialog(global.get().frame, "This file exists.  Overwrite?", "Overwrite?");

							if (optResult==JOptionPane.NO_OPTION)
								continue;

							if (optResult==JOptionPane.CANCEL_OPTION)
								return;
						}

						try
						{
							NetworkUtility.saveToFile(getNetworkContext().network,filename);
						}
						catch (final IOException exception)
						{
							NetworkContext.errors(exception.getMessage());

							return;
						}

						if (filename.getName().startsWith("@"))
							getNetworkContext().fileChooser.setSelectedFile(null);
						else
							getNetworkContext().currentFilename.set(some(filename));
					}

					return;
				}
			}
		};

		return new Runnable()
		{
			@Override
            public void run()
			{
				sideEffect.run(global.get().frame);
			}
		};
	}

	/**
	 * Called when the File-&gt;Exit menu item is selected.
	 */
	public static Runnable fileExit()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				assertNotNull(getNetworkContext());

				if (getNetworkContext().network.modified && !networkModifiedDialog())
					return;

				global.get().frame.setVisible(false);
				global.get().frame.dispose();

				System.exit(0);
			}
		};
	}

	public static Runnable zoomOut()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				final NetworkContext context2=getNetworkContext();
				context2.zoomLevel/=1.1;
				NetworkViewUtility.revalidate(context2);
			}
		};
	}

	public static Runnable zoomIn()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				final NetworkContext context2=getNetworkContext();
				context2.zoomLevel*=1.1;
				NetworkViewUtility.revalidate(context2);
			}
		};
	}

	public static Runnable zoomToFit()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				final NetworkContext context2=getNetworkContext();
				final JComponent view=context2.networkView;
				final Dimension optimumSize=NetworkViewUtility.getUnzoomedPreferredSize(context2);
				final Dimension actualSize=view.getVisibleRect().getSize();
				final double zoomFactorX=(double)optimumSize.width/(double)actualSize.width;
				final double zoomFactorY=(double)optimumSize.height/(double)actualSize.height;
				context2.zoomLevel=0.9/Math.max(zoomFactorX, zoomFactorY);
				NetworkViewUtility.revalidate(context2);
			}
		};
	}

	public static Runnable zoomOneToOne()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				final NetworkContext context2=getNetworkContext();
				context2.zoomLevel=1.0;
				NetworkViewUtility.revalidate(context2);
			}
		};
	}

	public static Runnable eventLogView()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				final Network network=getNetworkContext().network;
				network.log=Collections.add(network.log,"Viewed the event log.");
				EventLogDialogUtility.createEventLogDialog().setVisible(true);
			}
		};
	}

	public static Runnable eventLogClear()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				if (!getNetworkContext().userPermissions.allowClearingLog())
				{
					NetworkContext.errors("Cannot clear the event log during an assessment.");

					return;
				}

				getNetworkContext().network.log.clear();
			}
		};
	}

	public static Runnable eventLogSave()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				final int result=JFileChooserUtility.showSaveDialog(getNetworkContext().fileChooser, global.get().frame);

				if (result==JFileChooser.APPROVE_OPTION)
				{
					final File filename=getNetworkContext().fileChooser.getSelectedFile();

					Writer fileWriter=null;

					try
					{
						fileWriter=new FileWriter(filename);

						fileWriter.write(LogUtility.asString(getNetworkContext().network.log));
						fileWriter.close();
					}
					catch (final IOException exception)
					{
						throw new RuntimeException(exception);
					}
					finally
					{
						try
						{
							if (fileWriter!=null)
								fileWriter.close();
						}
						catch (final IOException exception)
						{
							exception.printStackTrace();
						}
					}
				}
			}
		};
	}

	public static void practiceTroubleshootingTest()
	{
		errors("Practice Troubleshooting Test not yet supported");
		//WebInterface.getTSExample();
	}

	public static Runnable practiceTest()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				final String[] choices=new String[]{"Easy","Medium","Hard"};
				final CustomJOptionPaneResult result=CustomJOptionPane.showLabelsAndConfirmation(global.get().frame, "Select a difficulty level", "Select a difficulty level", choices, 1, "Duplicate Test Conditions", new SideEffect<JDialog>()
				{
					@Override
                    public void run(final JDialog aDialog)
					{
					}
				});

				final String choice=result.getChoice();

				if (choice==null)
				{
					networkNew();
					return;
				}

				final Lazy<Problem> difficulty;
				if (Caster.equalT(choice, "Easy"))
					difficulty=ProblemDifficulty.EASY;
				else
					if (Caster.equalT(choice, "Hard"))
						difficulty=ProblemDifficulty.HARD;
					else
						difficulty=ProblemDifficulty.MEDIUM;

				if (result.confirmationTicked())
				{
					global.get().editProblemButton.setText("Upload Solution");
					getNetworkContext().userPermissions=UserPermissions.PRACTICE_TEST_SIMULATING_ACTUAL_TEST;
				}
				else
					getNetworkContext().userPermissions=UserPermissions.PRACTICE_TEST;

				getNetworkContext().network.problem=difficulty.invoke();
				JOptionPaneUtility.showMessageDialog(global.get().frame, getNetworkContext().network.problem.asString());
			}
		};
	}

	public static Runnable testConnectivity()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				ConnectivityTestDialogUtility.createConnectivityTestDialog(SideEffectUtility.<JDialog>doNothing());
			}
		};
	}

	public static Runnable helpContents()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				HelpFrameUtility.createHelpFrame().setVisible(true);
			}
		};
	}

	public static Runnable helpAbout()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				CustomJOptionPane.showNonModalMessageDialog(global.get().frame, "About IPSim", "IPSim is a network simulator, for teaching and assessment of skills in static subnetting.<p><p>Version "+Globals.appVersion+"<p>Build date: "+InitialDialogUtility.version+"<p>Design by Andrew Young and Ricky Clarkson<p>Programming by Ricky Clarkson (contributions from Andrew Young)<p><p>(c) University of Salford 2002-2006");
			}
		};
	}

	public static boolean networkModifiedDialog()
	{
		final String message;

		if (getNetworkContext().userPermissions==UserPermissions.ACTUAL_TEST)
			message="<html>CLOSING THE NETWORK WITHOUT UPLOADING WILL CAUSE YOU TO LOSE YOUR WORK.<br>CLICK CANCEL, THEN UPLOAD SOLUTION.<br>IF YOU ARE UNSURE, RAISE YOUR HAND.<br><br>This network has been modified.  Save changes?";
		else
			message="This network has been modified.  Save changes?";

		final int result=CustomJOptionPane.showYesNoCancelDialog(global.get().frame, message, "Confirm Lose Data?");

		if (result==JOptionPane.YES_OPTION)
		{
			fileSave().run();

			if (getNetworkContext().network.modified)
				return false;
		}

		return !(result==JOptionPane.CANCEL_OPTION);
	}

	public static void loadAssessmentProblem()
	{
		final String testNumber=JOptionPaneUtility.showInputDialog(global.get().frame, "Enter the test number given to you by your tutor");
		if (testNumber==null || testNumber.length()==0)
			return;

		getNetworkContext().testNumber=testNumber;

		MaybeUtility.run(WebInterface.getProblem(getNetworkContext().testNumber), new SideEffect<String>()
		{
			@Override
            public void run(final String string)
			{
				final String[] strings=string.split("\n");

				final String netSizesString=strings[1].split("=")[1];
				final String[] netSizes=netSizesString.split(",");

				final String chosenSize=netSizes[(int)(Math.random()*(double)netSizes.length)];

				final String subnetOptionString=strings[2].split("=")[1];
				final String[] subnetOptions=subnetOptionString.split(",");

				final String chosenSubnetOption=subnetOptions[(int)(Math.random()*(double)subnetOptions.length)];

				final ProblemBuilder problemBuilder=new ProblemBuilder();

				final ProblemBuilder.Stage2 problemStage2=EitherUtility.unsafeLeft(problemBuilder.withSubnets(parseInt(chosenSubnetOption)));

				IPAddress generateNetworkNumber;

				int giveUp=0;

				do
				{
					giveUp++;
					generateNetworkNumber=ProblemUtility.generateNetworkNumber(parseInt(chosenSize));
				}
				while (giveUp<100 && 0==(generateNetworkNumber.rawValue&0xFF00));

				final NetMask mask=NetMaskUtility.createNetMaskFromPrefixLength(parseInt(chosenSize));

				final Problem problem=EitherUtility.unsafeLeft(problemStage2.withNetBlock(new NetBlock(generateNetworkNumber, mask)));

				getNetworkContext().network.problem=problem;
				getNetworkContext().userPermissions=UserPermissions.ACTUAL_TEST;
				JOptionPaneUtility.showMessageDialog(global.get().frame, problem.asString());

				boolean set=false;

				do
				{
					final String userName=JOptionPane.showInputDialog(global.get().frame, "Please enter your University email address, e.g., "+"N.Other@student.salford.ac.uk");

					if (userName!=null && !(0==userName.length()))
					{
						getNetworkContext().emailAddress=userName;
						try
						{
							WebInterface.putSUProblem(userName, problem.asString());
						}
						catch (final IOException exception)
						{
							JOptionPane.showMessageDialog(global.get().frame, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
						set=true;
					}
				}
				while (!set);
			}
		});

	}

	public static Runnable testConformance()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				if (!getNetworkContext().userPermissions.allowFullTests().first())
				{
					NetworkContext.errors(getNetworkContext().userPermissions.allowFullTests().second());
					return;
				}

				final ConformanceTests.ResultsAndSummaryAndPercent results=ConformanceTests.allChecks(getNetworkContext().network);

				final String ifExists=results.percent+"% Conformance\n\n"+results.summary;
				final String ifNothing="No problem is set, so some checks cannot be performed\n\n"+results.percent+"\n\n"+results.summary;

				final Object message=getNetworkContext().network.problem==null ? ifNothing : ifExists;
				final int msgType=results.percent==100 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE;
				JOptionPane.showMessageDialog(global.get().frame, message, "Conformance Test", msgType);
			}
		};
	}

	public static SideEffect<ProgressMonitor> operationsCheckSolution()
	{
		return new SideEffect<ProgressMonitor>()
		{
			@Override
            public void run(final ProgressMonitor monitor)
			{
				final Network network=getNetworkContext().network;
				final ConnectivityResults connectivityResults=ConnectivityTest.testConnectivity(network,new SideEffect<String>()
				{
					@Override
                    public void run(final String s)
					{
						monitor.setNote(s);
					}
				},new SideEffect<Integer>()
				{
					@Override
                    public void run(final Integer integer)
					{
						monitor.setProgress(integer);
					}
				});

				final int connectivity=connectivityResults.getPercentConnected();

				final JLabel connectivityLabel=new JLabel("The network is "+connectivity+"% connected\n\n");
				final AbstractButton connectivityButton=new JButton("More info...");
				connectivityLabel.setLabelFor(connectivityButton);

				connectivityButton.addActionListener(new ActionListener()
				{
					@Override
                    public void actionPerformed(final ActionEvent e)
					{
						final String[] outputs=new String[connectivityResults.getOutputs().size()];
						connectivityResults.getOutputs().toArray(outputs);

						CustomJOptionPane.showNonModalMessageDialog(global.get().frame, "Connectivity Results", connectivity==100 ? "All pings succeeded." : Arrays.toString("<br>", outputs));
					}
				});

				monitor.setNote("Conformance Tests");

				final ConformanceTests.ResultsAndSummaryAndPercent results=ConformanceTests.allChecks(network);

				monitor.setProgress(100);

				final JLabel conformanceLabel=new JLabel(network.problem==null ? "Conformance: N/A (No problem set)\n\n" : "Conformance: "+results.percent+"%\n\n");

				final AbstractButton conformanceButton=new JButton("More Info...");

				conformanceLabel.setLabelFor(conformanceButton);
				conformanceButton.addActionListener(new ActionListener()
				{
					@Override
                    public void actionPerformed(final ActionEvent e)
					{
						CustomJOptionPane.showNonModalMessageDialog(global.get().frame, "Results", results.percent==100 ? "The network conforms with the requirements." : results.summary);
					}
				});

				final int overall=(results.percent+connectivity)/2;

				monitor.close();

				final int padding=20;

				final Container panel=new JPanel();

				AnyLayout.useAnyLayout(panel, 0.5f, 0.5f, new SizeCalculator()
				{
					@Override
                    public int getHeight()
					{
						final int labels=connectivityLabel.getPreferredSize().height+conformanceLabel.getPreferredSize().height;
						final int buttons=connectivityButton.getPreferredSize().height+conformanceButton.getPreferredSize().height;

						return padding*2+Math.max(labels, buttons);
					}

					@Override
                    public int getWidth()
					{
						final int top=connectivityLabel.getPreferredSize().width+connectivityButton.getPreferredSize().width;
						final int bottom=conformanceLabel.getPreferredSize().width+conformanceButton.getPreferredSize().width;
						return padding*3+Math.max(top, bottom);
					}
				}, null);

				panel.add(connectivityLabel, ConstraintUtility.topLeft(Function.constant(padding), Function.constant(padding)));
				panel.add(connectivityButton, ConstraintUtility.topRight(Function.constant(padding), Function.constant(padding)));
				panel.add(conformanceLabel, RelativeConstraints.below(connectivityLabel, padding));
				panel.add(conformanceButton, RelativeConstraints.levelWith(conformanceLabel, connectivityButton));

				CustomJOptionPane.showNonModalMessageDialog(global.get().frame, "Results", panel);

				network.log=Collections.add(network.log,"Checked solution: "+overall+"% score");
			}
		};
	}

	public static Runnable downloadConfiguration()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				try
				{
					String name;

					//TODO make this not an infinite loop in headless mode.
					do
					{
						name=JOptionPane.showInputDialog(global.get().frame, "Enter the name of the configuration");
						if (name==null)
							return;
					}
					while (name.length()==0);

					final String realName=name;
					final NamedConfiguration namedConfig=WebInterface.getNamedConfiguration(getNetworkContext().errors, realName);

					NetworkUtility.loadFromString(getNetworkContext().network, namedConfig.configuration);
					global.get().frame.setTitle("IPSim - "+namedConfig.name);
				}
				catch (final CheckedIllegalStateException exception1)
				{
				}
				catch (final NoSuchConfigurationException exception)
				{
				}
			}
		};
	}

	public static Runnable freeform()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				getNetworkContext().userPermissions=UserPermissions.FREEFORM;
			}
		};
	}

	public static Runnable clearAllArpTables()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				final Network network=getNetworkContext().network;
				network.log=Collections.add(network.log,"Cleared all ARP tables.");
				final Iterable<Computer> computers=NetworkUtility.getAllComputers(network);
				for (final Computer computer : computers)
					computer.arpTable.clear();
			}
		};
	}

	public static Runnable breakNetwork()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				BreakNetwork.breakNetwork();
			}
		};
	}

	public static void actualTroubleshootingTest()
	{
		NetworkContext.errors("The Actual Troubleshooting Test is not yet implemented");
	}

	public static Runnable setArpCacheTimeout()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				try
				{
					final int time=parseInt(JOptionPane.showInputDialog(getNetworkContext().networkView, "How long do you want the ARP cache to live for, in seconds?"));
					if (time>0)
						getNetworkContext().network.arpCacheTimeout=time;
				}
				catch (NumberFormatException e)
				{
				}
			}
		};
	}

	public static Runnable joinWithISP()
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				final List<NetworkContext> contexts=Global.getNetworkContexts();
				Network network=contexts.get(0).network;

				for (final NetworkContext context: contexts.subList(1,contexts.size()))
					network=Network.merge(network,context.network);

				for (final NetworkContext context: contexts)
					context.network=network;

				final Computer isp=ComputerFactory.newComputer(network,50,50);
				isp.isISP=true;

				for (final NetworkContext context: contexts)
				{
					final Card card=network.cardFactory.run(new Point(100,100));
					PositionUtility.setParent(network,card,0,isp,0);
					card.installDeviceDrivers(network);
					context.networkView.visibleComponents.add(card);
					context.networkView.visibleComponents.add(isp);
				}
			}
		};
	}
}