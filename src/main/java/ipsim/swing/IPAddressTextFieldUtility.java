package ipsim.swing;

import ipsim.network.ip.IPAddressUtility;
import java.awt.Color;
import javax.swing.JTextField;

public final class IPAddressTextFieldUtility {
    public static IPAddressTextField newInstance() {
        final JTextField textField = new JTextField(15);
        final IPAddressValidator validator = new IPAddressValidator(IPAddressUtility.zero);
        final ValidatingDocumentListener listener = new ValidatingDocumentListener(textField, textField.getBackground(), Color.pink, validator);

        textField.getDocument().addDocumentListener(listener);

        return new IPAddressTextField(validator, textField);
    }
}