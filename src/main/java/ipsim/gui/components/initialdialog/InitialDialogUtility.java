package ipsim.gui.components.initialdialog;

import anylayout.AnyLayout;
import anylayout.Constraint;
import anylayout.LayoutContext;
import anylayout.SizeCalculator;
import anylayout.extras.ConstraintBuilder;
import anylayout.extras.ConstraintUtility;
import anylayout.extras.PercentConstraints;
import anylayout.extras.PercentConstraintsUtility;
import anylayout.extras.RelativeConstraints;
import fpeas.function.Function;
import fpeas.function.FunctionUtility;
import ipsim.Global;
import ipsim.Globals;
import ipsim.gui.MenuHandler;
import ipsim.image.ImageLoader;
import ipsim.lang.Maths;
import ipsim.swing.Dialogs;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import org.jetbrains.annotations.NotNull;

import static anylayout.extras.ConstraintBuilder.preferredSize;
import static ipsim.awt.ComponentUtility.centreOnParent;

public class InitialDialogUtility
{
	public static final String version;

    static {
        final InputStream is = InitialDialogUtility.class.getResourceAsStream("/META-INF/maven/ipsim/ipsim/pom.properties");
        String tempVersion = "0.0.0-dev";
        if (is != null) {
            final Properties properties = new Properties();

            try {
                properties.load(is);
                tempVersion = properties.getProperty("version");
            } catch (IOException e) {
                e.printStackTrace();
                tempVersion = "0.0.0-dev";
            }
        }
        version = tempVersion;
    }
	public static InitialDialog createInitialDialog()
	{
		final JDialog maybeDialog=Dialogs.createDialogWithEscapeKeyToClose(Global.global.get().frame, "Welcome to IPSim");

		return impl(maybeDialog);
	}

	public static InitialDialog impl(final JDialog dialog)
	{
		final ImageIcon icon=ImageLoader.loadImage(InitialDialogUtility.class.getResource("/images/initial-dialog-background.jpg"));

		final JLabel jLabel=new JLabel(icon);

		dialog.setContentPane(jLabel);

		final Container contentPane=dialog.getContentPane();

		final JLabel introLabel=new JLabel("<html><body><center><h1>Welcome to IPSim "+Globals.appVersion+"</h1><p>IPSim is Copyright 2010 University of Salford and Ricky Clarkson and is BSD-Licenced.<br>Please choose from the following options, or click on the Help button.<br>Build-date "+version+"</center></p></body></html>", SwingConstants.CENTER);
		introLabel.setHorizontalAlignment(SwingConstants.CENTER);

		final JRadioButton freeformRadioButton=new JRadioButton("Practice");
		freeformRadioButton.setMnemonic(KeyEvent.VK_P);
		freeformRadioButton.setOpaque(false);
		freeformRadioButton.setSelected(true);

		final Font font=freeformRadioButton.getFont().deriveFont(14.0F);
		freeformRadioButton.setFont(font);

		final JRadioButton takeTestRadioButton=new JRadioButton("Actual Setup Test");
		takeTestRadioButton.setMnemonic(KeyEvent.VK_T);
		takeTestRadioButton.setFont(font);
		takeTestRadioButton.setOpaque(false);

		final JRadioButton practiceTroubleshootingTest=new JRadioButton("Practice Troubleshooting Test");
		practiceTroubleshootingTest.setOpaque(false);
		practiceTroubleshootingTest.setFont(font);

		final JRadioButton actualTroubleshootingTest=new JRadioButton("Actual Troubleshooting Test");
		actualTroubleshootingTest.setOpaque(false);
		actualTroubleshootingTest.setFont(font);

		final JRadioButton practiceTestRadioButton=new JRadioButton("Practice Setup Test");
		practiceTestRadioButton.setFont(font);
		practiceTestRadioButton.setMnemonic(KeyEvent.VK_C);
		practiceTestRadioButton.setOpaque(false);

		final JButton okButton=new JButton(" OK ");
		okButton.setMnemonic(KeyEvent.VK_O);

		final JButton helpButton=new JButton("Help");
		helpButton.setMnemonic(KeyEvent.VK_H);

		final JButton cancelButton=new JButton("Exit");
		cancelButton.setMnemonic(KeyEvent.VK_X);

		cancelButton.addActionListener(new ActionListener()
		{
			@Override
            public void actionPerformed(final ActionEvent event)
			{
				MenuHandler.fileExit().run();
			}
		});

		final int PADDING=10;

		AnyLayout.useAnyLayout(contentPane, 0.5f, 0.5f, new SizeCalculator()
		{
			@Override
            public int getHeight()
			{
				return introLabel.getPreferredSize().height+freeformRadioButton.getPreferredSize().height+practiceTestRadioButton.getPreferredSize().height+takeTestRadioButton.getPreferredSize().height+okButton.getPreferredSize().height+PADDING*5;
			}

			@Override
            public int getWidth()
			{
				final int widestRadioButtonOnLeft=Maths.max(freeformRadioButton.getPreferredSize().width, practiceTestRadioButton.getPreferredSize().width, takeTestRadioButton.getPreferredSize().width);
				final int widestRadioButtonOnRight=Maths.max(practiceTroubleshootingTest.getPreferredSize().width+practiceTroubleshootingTest.getPreferredSize().height);

				return Maths.max(PADDING*2+introLabel.getPreferredSize().width, PADDING*3+widestRadioButtonOnLeft+widestRadioButtonOnRight, PADDING*4+okButton.getPreferredSize().width+helpButton.getPreferredSize().width+cancelButton.getPreferredSize().width);
			}

		}, ConstraintUtility.typicalDefaultConstraint(new Runnable()
		{
			@Override
            public void run()
			{
				throw new RuntimeException();
			}
		}));

		final PercentConstraints constraints=PercentConstraintsUtility.newInstance(contentPane);

		final Constraint introLabelConstraint=ConstraintUtility.topCentre(PADDING);
		contentPane.add(introLabel, introLabelConstraint);

		final Function<LayoutContext, Integer> paddingFunction=FunctionUtility.constant(PADDING);

		final Constraint freeformConstraint=ConstraintBuilder.buildConstraint().setLeft(paddingFunction).setTop(new Function<LayoutContext, Integer>()
		{
			@Override
            @NotNull
			public Integer run(@NotNull final LayoutContext layoutContext)
			{
				return layoutContext.getLayoutInfo(introLabel).getFarOffset()+PADDING*3;
			}
		}).setWidth(preferredSize).setHeight(preferredSize);

		contentPane.add(freeformRadioButton, freeformConstraint);

		final Constraint practiceTestConstraints=RelativeConstraints.below(freeformRadioButton, PADDING);

		contentPane.add(practiceTestRadioButton, practiceTestConstraints);

		contentPane.add(takeTestRadioButton, RelativeConstraints.below(practiceTestRadioButton, PADDING));

		final Constraint takeTestConstraints=ConstraintBuilder.buildConstraint().setLeft(new Function<LayoutContext, Integer>()
		{
			@Override
            @NotNull
			public Integer run(@NotNull final LayoutContext layoutContext)
			{
				return layoutContext.getParentSize()-10-layoutContext.getPreferredSize();
			}
		}).setTop(new Function<LayoutContext, Integer>()
		{
			@Override
            @NotNull
			public Integer run(@NotNull final LayoutContext layoutContext)
			{
				return layoutContext.getLayoutInfo(practiceTestRadioButton).getOffset();
			}
		}).setWidth(preferredSize).setHeight(preferredSize);

		contentPane.add(practiceTroubleshootingTest, takeTestConstraints);

		contentPane.add(actualTroubleshootingTest, RelativeConstraints.below(practiceTroubleshootingTest, PADDING));

		final ButtonGroup group=new ButtonGroup();
		group.add(freeformRadioButton);
		group.add(practiceTestRadioButton);
		group.add(takeTestRadioButton);

		constraints.add(okButton, 10, 85, 15, 10, false, false);

		okButton.addActionListener(new ActionListener()
		{
			@Override
            public void actionPerformed(final ActionEvent e)
			{
				Runnable doLater=MenuHandler.freeform();

				if (practiceTestRadioButton.isSelected())
					doLater=new Runnable()
					{
						@Override
                        public void run()
						{
							MenuHandler.practiceTest().run();
						}
					};

				if (takeTestRadioButton.isSelected())
					doLater=new Runnable()
					{
						@Override
                        public void run()
						{
							MenuHandler.loadAssessmentProblem();
						}
					};

				if (practiceTroubleshootingTest.isSelected())
					doLater=new Runnable()
					{
						@Override
                        public void run()
						{
							MenuHandler.practiceTroubleshootingTest();
						}
					};

				if (actualTroubleshootingTest.isSelected())
					doLater=new Runnable()
					{
						@Override
                        public void run()
						{
							MenuHandler.actualTroubleshootingTest();
						}
					};

				dialog.setVisible(false);
				dialog.dispose();

				doLater.run();
			}
		});

		constraints.add(helpButton, 40, 85, 20, 10, false, false);

		helpButton.addActionListener(new ActionListener()
		{
			@Override
            public void actionPerformed(final ActionEvent event)
			{
				dialog.dispose();
				MenuHandler.freeform().run();
				MenuHandler.helpContents().run();
			}
		});

		constraints.add(cancelButton, 70, 85, 20, 10, false, false);

		dialog.setResizable(false);

		dialog.pack();
		centreOnParent(dialog, Global.global.get().frame);

		return new InitialDialog(dialog, okButton, practiceTestRadioButton);
	}
}