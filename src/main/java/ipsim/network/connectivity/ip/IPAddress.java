package ipsim.network.connectivity.ip;

import ipsim.lang.Stringable;
import ipsim.network.ip.IPAddressUtility;

public final class IPAddress implements Stringable {
    public final int rawValue;

    public IPAddress(final int rawValue) {
        this.rawValue = rawValue;
    }

    @Override
    public String asString() {
        return IPAddressUtility.toString(rawValue);
    }

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof IPAddress))
            return false;

        final IPAddress address = (IPAddress) object;
        return rawValue == address.rawValue;
    }

    @Override
    public int hashCode() {
        return 38659 + 17 * rawValue;
    }

    public int bitwiseAnd(final NetMask netMask) {
        return rawValue & netMask.rawValue;
    }
}