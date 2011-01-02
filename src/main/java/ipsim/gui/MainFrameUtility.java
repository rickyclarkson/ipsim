package ipsim.gui;

import anylayout.AnyLayout;
import anylayout.extras.ConstraintUtility;
import anylayout.extras.PercentConstraints;
import anylayout.extras.PercentConstraintsUtility;
import anylayout.extras.SizeCalculatorUtility;
import fj.Effect;
import ipsim.Global;
import ipsim.gui.components.ProblemDialog;
import ipsim.gui.components.ScrapbookDialogUtility;
import ipsim.image.ImageLoader;
import ipsim.lang.Assertion;
import ipsim.lang.Runnables;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.hub.ProgressMonitor;
import ipsim.swing.CustomJOptionPane;
import ipsim.swing.SwingWorker;
import ipsim.webinterface.WebInterface;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.StringWriter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import static ipsim.NetworkContext.errors;
import static ipsim.gui.MenuHandler.zoomIn;
import static ipsim.gui.MenuHandler.zoomOneToOne;
import static ipsim.gui.MenuHandler.zoomOut;
import static ipsim.gui.MenuHandler.zoomToFit;
import static ipsim.gui.StandardToolBarUtility.createStandardToolBar;
import static ipsim.gui.UserPermissions.ACTUAL_TEST;
import static ipsim.gui.UserPermissions.PRACTICE_TEST_SIMULATING_ACTUAL_TEST;
import static ipsim.gui.components.ContextMenuUtility.item;
import static ipsim.swing.Buttons.newButton;
import static java.awt.Cursor.WAIT_CURSOR;
import static java.awt.Cursor.getPredefinedCursor;
import static java.lang.System.setProperty;

public class MainFrameUtility
{
	public static JFrame createMainFrame()
	{
		final JFrame frame=new JFrame();
		initialise(frame);
		return frame;
	}

	public static void initialise(final JFrame frame)
	{
		setProperty("swing.aatext", "true");
		setProperty("apple.laf.useScreenMenuBar", "true");

		frame.setTitle("IPSim");

		final Container rootPanel=new JPanel(new BorderLayout());

		frame.setSize(800, 600);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);

		final ImageIcon icon=ImageLoader.loadImage(MainFrameUtility.class.getResource("/images/icon.png"));

		frame.setIconImage(icon.getImage());

		setupMenus(frame);
		setupCloseHandling(frame);

		final Container standardToolBar=createStandardToolBar();

		rootPanel.add(standardToolBar, BorderLayout.NORTH);

		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
            public void run()
			{
				Assertion.assertNotNull(rootPanel);
				final Container componentsBar=ComponentToolBarUtility.newBar();

				rootPanel.add(componentsBar, BorderLayout.WEST);

				final JPanel helpPanel=new JPanel();
				HelpFrameUtility.createPane.e(helpPanel);
				final JPanel scrapbook=ScrapbookDialogUtility.createScrapbook();
				scrapbook.setOpaque(false);
				//context.tabbedPane.addTab("Scrapbook", scrapbook);

				final JPanel eventLogPanel=new JPanel();
				AnyLayout.useAnyLayout(eventLogPanel, 0.5f, 0.5f, SizeCalculatorUtility.absoluteSize(800, 600), ConstraintUtility.typicalDefaultConstraint(Runnables.throwRuntimeException));
				final PercentConstraints constraints=PercentConstraintsUtility.newInstance(eventLogPanel);
				EventLogDialogUtility.mutatePane(constraints);
				//context.tabbedPane.addTab("Event Log", eventLogPanel);
				//context.tabbedPane.addTab("Help", helpPanel);
				rootPanel.add(Global.global.get().tabbedPane, BorderLayout.CENTER);

				final Container panel=new JPanel(new BorderLayout());

				final JButton editProblemButton=new JButton("Edit Problem");
				editProblemButton.setFocusable(false);

				editProblemButton.addActionListener(new ActionListener()
				{
					@Override
                    public void actionPerformed(final ActionEvent e)
					{
						if (Global.getNetworkContext().userPermissions==ACTUAL_TEST)
						{
							final String userName=Global.getNetworkContext().emailAddress;

							final StringWriter writer=new StringWriter();

							NetworkUtility.saveToWriter(Global.getNetworkContext().network, writer);

							try
							{
								String putSUSolution=WebInterface.putSUSolution(Global.getNetworkContext().testNumber, userName, writer.toString());

								if ((int)'1'==(int)putSUSolution.charAt(0))
									putSUSolution="Upload completed.  You may also want to use 'Save As' to save a copy on a removable disc";

								CustomJOptionPane.showNonModalMessageDialog(Global.global.get().frame, "Solution Uploaded", putSUSolution);
							}
							catch (final IOException exception)
							{
								errors(exception.getMessage());
							}
						}
						else
							if (Global.getNetworkContext().userPermissions==PRACTICE_TEST_SIMULATING_ACTUAL_TEST)
								errors("In the real test, you will upload your solution to a central server, from where it will be marked.  You can upload as many times as you like; only the latest version will be marked.");
							else
								ProblemDialog.createProblemDialog().setVisible(true);
					}
				});

				panel.add(editProblemButton, BorderLayout.WEST);

				Global.global.get().statusBar.setHorizontalAlignment(SwingConstants.CENTER);

				panel.add(Global.global.get().statusBar, BorderLayout.CENTER);

				final Effect<JFrame> sideEffect=new Effect<JFrame>()
				{
					@Override
                    public void e(final JFrame realFrame)
					{
						final Cursor original=realFrame.getCursor();

						if (!Global.getNetworkContext().userPermissions.allowFullTests()._1())
						{
							MenuHandler.testConnectivity().run();
							return;
						}

						realFrame.setCursor(getPredefinedCursor(WAIT_CURSOR));

						final ProgressMonitor monitor=new ProgressMonitor(realFrame, "Running tests                    ", "Running tests...                   ", 0, 100);

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
									Thread.currentThread().setUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler());

									try
									{
										MenuHandler.operationsCheckSolution().e(monitor);
									}
									finally
									{
										realFrame.setCursor(original);
									}
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

								return null;
							}
						}.execute();
					}
				};

				final Runnable runnable=new Runnable()
				{
					@Override
                    public void run()
					{
						sideEffect.e(Global.global.get().frame);
					}
				};

				panel.add(newButton("Check Solution", runnable), BorderLayout.EAST);

				rootPanel.add(panel, BorderLayout.SOUTH);

				Global.global.get().frame.setContentPane(rootPanel);

				Global.global.get().frame.repaint();
			}
		});
	}

	/**
	 * Defines the actions taken when the main window is closed by the user.
	 */
	public static void setupCloseHandling(final JFrame frame)
	{
		Assertion.assertNotNull(frame);

		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		frame.addWindowListener(new WindowListener()
		{
			@Override
            public void windowOpened(final WindowEvent e)
			{
			}

			@Override
            public void windowClosing(final WindowEvent e)
			{
				MenuHandler.fileExit().run();
			}

			@Override
            public void windowClosed(final WindowEvent e)
			{
			}

			@Override
            public void windowIconified(final WindowEvent e)
			{
			}

			@Override
            public void windowDeiconified(final WindowEvent e)
			{
			}

			@Override
            public void windowActivated(final WindowEvent e)
			{
			}

			@Override
            public void windowDeactivated(final WindowEvent e)
			{
			}
		});
	}

	public static void setupMenus(final JFrame frame)
	{
		final JMenuBar bar=new JMenuBar();

		bar.add(setupFileMenu());
		bar.add(setupZoomMenu());
		bar.add(setupEventLogMenu());
		bar.add(setupToolsMenu());
		bar.add(setupHelpMenu());

		setJMenuBar(frame, bar);
	}

	public static void setJMenuBar(final JFrame frame, final JMenuBar bar)
	{
		frame.setJMenuBar(bar);
	}


	public static Component setupHelpMenu()
	{
		final JMenu helpMenu=new JMenu("Help");
		helpMenu.setMnemonic('H');

		final JMenuItem contents=item("Contents", 'C', MenuHandler.helpContents());
		contents.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

		helpMenu.add(contents);
		helpMenu.add(item("About", 'A', MenuHandler.helpAbout()));

		return helpMenu;
	}

	public static Component setupToolsMenu()
	{
		final JMenu toolsMenu=new JMenu("Tools");
		toolsMenu.setMnemonic('T');
		toolsMenu.add(item("Test Connectivity", 'C', KeyEvent.VK_C, MenuHandler.testConnectivity()));
		toolsMenu.add(item("Test Conformance", 'F', KeyEvent.VK_F, MenuHandler.testConformance()));
		toolsMenu.add(item("Clear all ARP Tables", 'A', MenuHandler.clearAllArpTables()));
		toolsMenu.add(item("Break Network", 'B', MenuHandler.breakNetwork()));
		toolsMenu.add(item("Set ARP Cache Timeout", 'A', MenuHandler.setArpCacheTimeout()));
		return toolsMenu;
	}

	public static Component setupZoomMenu()
	{
		final JMenu zoomMenu=new JMenu("Zoom");
		zoomMenu.setMnemonic('Z');

		zoomMenu.add(item("Zoom Out", 'O', KeyEvent.VK_MINUS, zoomOut()));
		zoomMenu.add(item("Zoom In", 'I', KeyEvent.VK_PLUS, zoomIn()));
		zoomMenu.add(item("Zoom 1:1", '1', KeyEvent.VK_1, zoomOneToOne()));
		zoomMenu.add(item("Zoom Auto", 'A', zoomToFit()));

		return zoomMenu;
	}

	public static Component setupEventLogMenu()
	{
		final JMenu eventLogMenu=new JMenu("Event Log");
		eventLogMenu.setMnemonic('L');

		eventLogMenu.add(item("View", 'V', MenuHandler.eventLogView()));
		eventLogMenu.add(item("Save as Text", 'S', MenuHandler.eventLogSave()));

		final JMenuItem clearLogItem=item("Clear", 'C', MenuHandler.eventLogClear());

		eventLogMenu.add(clearLogItem);

		return eventLogMenu;
	}

	public static Component setupFileMenu()
	{
		final JMenu fileMenu=new JMenu("Network");
		fileMenu.setMnemonic('N');

		fileMenu.add(item("New", 'N', KeyEvent.VK_N, MenuHandler.networkNew()));
		fileMenu.add(item("Open...", 'O', KeyEvent.VK_O, MenuHandler.fileOpen()));
		fileMenu.add(item("Save", 'S', KeyEvent.VK_S, MenuHandler.fileSave()));
		fileMenu.add(item("Save As...", 'A', MenuHandler.fileSaveAs()));
		fileMenu.add(item("Join Networks with ISP",'J',MenuHandler.joinWithISP()));
		fileMenu.add(item("Exit", 'X', MenuHandler.fileExit()));

		return fileMenu;
	}
}