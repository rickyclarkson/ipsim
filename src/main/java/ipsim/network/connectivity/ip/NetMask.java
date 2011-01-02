package ipsim.network.connectivity.ip;

import ipsim.Caster;
import ipsim.network.InvalidNetMaskException;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.IPAddressUtility;

public final class NetMask {
    public final int rawValue;

    public NetMask(final int rawValue) {
        this.rawValue = rawValue;
    }

    public static String asString(final int rawValue) {
        return IPAddressUtility.toString(rawValue);
    }

    @Override
    public String toString() {
        return asString(rawValue);
    }

    public static String asCustomString(final NetMask netMask) {
        try {
            return String.valueOf(NetMaskUtility.getPrefixLength(netMask));
        } catch (final InvalidNetMaskException exception) {
            return asString(netMask.rawValue);
        }
    }

    @Override
    public boolean equals(final Object object) {
        if (!Caster.isNetMask(object))
            return false;

        return rawValue == Caster.asNetMask(object).rawValue;
    }

    @Override
    public int hashCode() {
        return 23459 + 37 * rawValue;
    }
}