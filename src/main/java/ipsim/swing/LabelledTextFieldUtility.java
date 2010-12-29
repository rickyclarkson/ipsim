package ipsim.swing;

import static anylayout.AnyLayout.useAnyLayout;
import anylayout.LayoutContext;
import anylayout.SizeCalculator;
import anylayout.extras.ConstraintUtility;
import static anylayout.extras.ConstraintUtility.typicalDefaultConstraint;
import anylayout.extras.RelativeConstraints;
import fpeas.function.Function;
import fpeas.function.FunctionUtility;
import static ipsim.lang.Runnables.throwRuntimeException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;

public final class LabelledTextFieldUtility
{
	private LabelledTextFieldUtility()
	{
	}

	public static LabelledTextField createLabelledTextField(final String text,final JTextField field)
	{
		final JPanel panel=new JPanel();
		panel.setOpaque(false);

		final JLabel label=new JLabel(text);
		label.setLabelFor(field);
		panel.add(label);
		panel.add(field);

		return new LabelledTextField()
		{
			@Override
            public Component getPanel()
			{
				return panel;
			}

		};
	}

	public static LabelledTextField createLabelledTextField2(final String text,final JTextField field)
	{
		final JPanel panel=new JPanel();
		panel.setOpaque(false);

		final JLabel label=new JLabel(text);

		useAnyLayout(panel,0.5f,0.5f,new SizeCalculator()
		{
			@Override
            public int getHeight()
			{
				return Math.max(field.getPreferredSize().height,label.getPreferredSize().height);
			}

			@Override
            public int getWidth()
			{
				return field.getPreferredSize().width+label.getPreferredSize().width+5;
			}

		},typicalDefaultConstraint(throwRuntimeException));

		final Function<LayoutContext,Integer> constant=FunctionUtility.constant(0);
		panel.add(label,ConstraintUtility.topLeft(constant,constant));
		panel.add(field,RelativeConstraints.rightOf(label,5));

		return new LabelledTextField()
		{
			@Override
            public Component getPanel()
			{
				return panel;
			}

		};
	}
}