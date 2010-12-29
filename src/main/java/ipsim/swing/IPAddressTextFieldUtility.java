package ipsim.swing;

import ipsim.network.ip.IPAddressUtility;

import javax.swing.JTextField;
import java.awt.Color;

public final class IPAddressTextFieldUtility
{
	public static IPAddressTextField newInstance()
	{
		final JTextField textField=new JTextField(15);
		final IPAddressValidator validator=new IPAddressValidator(IPAddressUtility.zero);
		final ValidatingDocumentListener listener=new ValidatingDocumentListener(textField,textField.getBackground(),Color.pink,validator);

		textField.getDocument().addDocumentListener(listener);

		return new IPAddressTextField(validator, textField);
	}
}