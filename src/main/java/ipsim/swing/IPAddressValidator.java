package ipsim.swing;

import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public final class IPAddressValidator implements DocumentValidator {
    private IPAddress address;

    public IPAddressValidator(final IPAddress address) {
        this.address = address;
    }

    @Override
    public boolean isValid(final Document document) {
        final String string;

        try {
            string = document.getText(0, document.getLength());
        } catch (final BadLocationException exception) {
            throw new RuntimeException(exception);
        }

        try {
            address = IPAddressUtility.valueOf(string);
            return true;
        } catch (final CheckedNumberFormatException exception) {
            return false;
        }
    }

    public void setIPAddress(final IPAddress ipAddress) {
        this.address = ipAddress;
    }

    public IPAddress getAddress() {
        return address;
    }
}