package ipsim;

import static ipsim.ExceptionHandler.createExceptionHandler;
import ipsim.gui.MainFrameUtility;
import static ipsim.gui.MenuHandler.networkNew;

import javax.swing.*;
import java.awt.*;
import static java.lang.Thread.setDefaultUncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The main class to launch IPSim with.
 */
public class Main
{
	public static void main(final String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
            public void run()
			{
				realMain();
			}
		});
	}

	public static void realMain()
	{
		Toolkit.getDefaultToolkit().setDynamicLayout(true);

		try
		{
			System.setErr(System.out);

			final String className=UIManager.getSystemLookAndFeelClassName();

			if (!className.toLowerCase(Locale.ENGLISH).contains("gtk"))
				UIManager.setLookAndFeel(className);

			final List<Object> gradients=new ArrayList<Object>(5);
			gradients.add(1.00f);
			gradients.add(0.00f);
			gradients.add(new Color(0xFDFDFB));
			gradients.add(new Color(0xE4E3D4));
			gradients.add(new Color(0xB8CFE5));

			final UIDefaults lookAndFeelDefaults=UIManager.getLookAndFeelDefaults();
			lookAndFeelDefaults.put("Button.gradient", gradients);
			lookAndFeelDefaults.put("ToggleButton.gradient", gradients);
			lookAndFeelDefaults.put("swing.boldMetal", false);
			lookAndFeelDefaults.put("swing.aatext", true);
		}
		catch (final ClassNotFoundException exception)
		{
			throw new RuntimeException(exception);
		}
		catch (final InstantiationException exception)
		{
			throw new RuntimeException(exception);
		}
		catch (final IllegalAccessException exception)
		{
			throw new RuntimeException(exception);
		}
		catch (final UnsupportedLookAndFeelException exception)
		{
			throw new RuntimeException(exception);
		}

		setDefaultUncaughtExceptionHandler(createExceptionHandler());

		Global.global.get().frame=MainFrameUtility.createMainFrame();
		Global.global.get().frame.setVisible(true);

		networkNew().run();
	}
}