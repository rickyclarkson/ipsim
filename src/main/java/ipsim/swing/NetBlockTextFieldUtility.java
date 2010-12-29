package ipsim.swing;

import static fpeas.either.EitherUtility.unsafeLeft;
import fpeas.lazy.Lazy;
import ipsim.gui.components.NetBlockTextField;
import static ipsim.network.ethernet.NetBlockUtility.createNetBlock;
import ipsim.network.ethernet.NetBlock;
import ipsim.textmetrics.TextMetrics;

import javax.swing.*;
import java.awt.*;

public final class NetBlockTextFieldUtility
{
	private NetBlockTextFieldUtility()
	{
	}

	public static NetBlockTextField createNetBlockTextField()
	{
		final JTextField textField=new JTextField()
		{
			private static final long serialVersionUID=-8215166609438040479L;

			@Override
			public Dimension getPreferredSize()
			{
				return new Dimension
						(
								TextMetrics.getWidth(getFont(), "999.999.999.999/99")+13,
								(int)super.getPreferredSize().getHeight()
						);
			}
		};

		final NetBlockValidator validator=new NetBlockValidator(unsafeLeft(createNetBlock("0.0.0.0/0")));

		final ValidatingDocumentListener listener=new ValidatingDocumentListener(textField, UIManager.getColor("TextField.background"), Color.pink, validator);

		textField.getDocument().addDocumentListener(listener);

		return instance(validator, textField);
	}

	public static NetBlockTextField instance(final NetBlockValidator validator, final JTextField textField)
	{
		return new NetBlockTextField()
		{
			public final Lazy<NetBlock> netBlock=new Lazy<NetBlock>()
			{
				@Override
                public NetBlock invoke()
				{
					return validator.getNetBlock();
				}

			};

			@Override
            public Lazy<NetBlock> netBlock()
			{
				return netBlock;
			}

			@Override
            public boolean isValid()
			{
				return validator.isValid(textField.getDocument());
			}

			@Override
            public JTextField getComponent()
			{
				return textField;
			}
		};
	}
}