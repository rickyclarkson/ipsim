package ipsim.swing;

import anylayout.LayoutContext;
import anylayout.SizeCalculator;
import anylayout.extras.ConstraintUtility;
import anylayout.extras.RelativeConstraints;
import fj.F;
import fj.Function;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import static anylayout.AnyLayout.useAnyLayout;
import static anylayout.extras.ConstraintUtility.typicalDefaultConstraint;
import static ipsim.lang.Runnables.throwRuntimeException;

public final class LabelledTextFieldUtility {
    private LabelledTextFieldUtility() {
    }

    public static LabelledTextField createLabelledTextField(final String text, final JTextField field) {
        final JPanel panel = new JPanel();
        panel.setOpaque(false);

        final JLabel label = new JLabel(text);
        label.setLabelFor(field);
        panel.add(label);
        panel.add(field);

        return new LabelledTextField() {
            @Override
            public Component getPanel() {
                return panel;
            }

        };
    }

    public static LabelledTextField createLabelledTextField2(final String text, final JTextField field) {
        final JPanel panel = new JPanel();
        panel.setOpaque(false);

        final JLabel label = new JLabel(text);

        useAnyLayout(panel, 0.5f, 0.5f, new SizeCalculator() {
            @Override
            public int getHeight() {
                return Math.max(field.getPreferredSize().height, label.getPreferredSize().height);
            }

            @Override
            public int getWidth() {
                return field.getPreferredSize().width + label.getPreferredSize().width + 5;
            }

        }, typicalDefaultConstraint(throwRuntimeException));

        final F<LayoutContext, Integer> constant = Function.constant(0);
        panel.add(label, ConstraintUtility.topLeft(constant, constant));
        panel.add(field, RelativeConstraints.rightOf(label, 5));

        return new LabelledTextField() {
            @Override
            public Component getPanel() {
                return panel;
            }

        };
    }
}