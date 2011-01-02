package ipsim.swing;

import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ip.IPAddressUtility;
import javax.swing.JTextField;

public class IPAddressTextField {
    private final IPAddressValidator validator;
    public final JTextField textField;

    public IPAddressTextField(final IPAddressValidator validator, final JTextField textField) {
        this.validator = validator;
        this.textField = textField;
    }

    public IPAddress getIPAddress() {
        return validator.getAddress();
    }

    public void setIPAddress(final IPAddress address) {
        validator.setIPAddress(address);

        if (0 == address.rawValue)
            textField.setText("");
        else
            textField.setText(IPAddressUtility.toString(address.rawValue));
    }
}