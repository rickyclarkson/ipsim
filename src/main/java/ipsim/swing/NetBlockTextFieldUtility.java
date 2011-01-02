package ipsim.swing;

import fj.P1;
import ipsim.gui.components.NetBlockTextField;
import ipsim.network.ethernet.NetBlock;
import ipsim.textmetrics.TextMetrics;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.UIManager;

import static ipsim.network.ethernet.NetBlockUtility.createNetBlock;

public final class NetBlockTextFieldUtility {
    private NetBlockTextFieldUtility() {
    }

    public static NetBlockTextField createNetBlockTextField() {
        final JTextField textField = new JTextField() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(TextMetrics.getWidth(getFont(), "999.999.999.999/99") + 13, (int) super.getPreferredSize().getHeight());
            }
        };

        final NetBlockValidator validator = new NetBlockValidator(createNetBlock("0.0.0.0/0").left().value());

        final ValidatingDocumentListener listener = new ValidatingDocumentListener(textField, UIManager.getColor("TextField.background"), Color.pink, validator);

        textField.getDocument().addDocumentListener(listener);

        return instance(validator, textField);
    }

    public static NetBlockTextField instance(final NetBlockValidator validator, final JTextField textField) {
        return new NetBlockTextField() {
            public final P1<NetBlock> netBlock = new P1<NetBlock>() {
                @Override
                public NetBlock _1() {
                    return validator.getNetBlock();
                }

            };

            @Override
            public P1<NetBlock> netBlock() {
                return netBlock;
            }

            @Override
            public boolean isValid() {
                return validator.isValid(textField.getDocument());
            }

            @Override
            public JTextField getComponent() {
                return textField;
            }
        };
    }
}