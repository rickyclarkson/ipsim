package ipsim.network.ethernet;

import ipsim.ExceptionHandler;
import ipsim.network.InvalidNetMaskException;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;

/**
 * This basically exists to make sure that netmasks and ip addresses don't get
 * mixed up.
 * <p/>
 * Logically, a netmask is an ip address, but is used for a different purpose.
 */
public final class NetMaskUtility {
    public static final NetMask zero = new NetMask(0);

    private NetMaskUtility() {
    }

    public static NetMask createNetMaskFromPrefixLength(final int prefixLength) {
        if (prefixLength == 0)
            return zero;

        if (prefixLength == 32)
            return getNetMask(0xFFFFFFFF);

        if (prefixLength < 0 || prefixLength > 32)
            throw new IllegalArgumentException(prefixLength + " outside the valid range - 0..32");

        return getNetMask(~(~0 >>> prefixLength));
    }

    public static NetMask getNetMask(final int rawValue) {
        return new NetMask(rawValue);
    }

    /**
     * @return true if the net mask is of the form 111...0000, i.e., a
     *         standard netmask.
     */
    public static boolean isValid(final NetMask netmask) {
        // 255.255.255.255 = -1 - signed int
        // 255.255.255.254 = -2
        // 255.255.255.252 = -4
        // x.x.x.x = -some power of two

        final int rawValue = netmask.rawValue;
        // TODO - do the following without bashing the heap
        final String string = Integer.toBinaryString(-rawValue);

        // preferably just with int arithmetic.
        boolean foundFirst = false;

        for (int a = 0; a < string.length(); a++)
            if ((int) '1' == (int) string.charAt(a)) {
                if (foundFirst)
                    return false;
                foundFirst = true;
            }

        return true;
    }

    public static NetMask valueOf(final String text) throws CheckedNumberFormatException {
        try {
            return getNetMask(IPAddressUtility.valueOf(text).rawValue);
        } catch (final CheckedNumberFormatException exception) {
            throw new CheckedNumberFormatException(text + " is not a valid netmask");
        }
    }

    public static int getPrefixLength(final NetMask netMask) throws InvalidNetMaskException {
        for (int a = 1; a < 32; a++) {
            final NetMask tempNetMask = createNetMaskFromPrefixLength(a);

            if (netMask.rawValue == tempNetMask.rawValue)
                return a;
        }

        throw new InvalidNetMaskException(NetMask.asString(netMask.rawValue) + " is not a valid netmask");
    }

    public static NetMask randomNetMask() {
        return createNetMaskFromPrefixLength((int) (Math.random() * 24 + 7));
    }

    public static NetMask valueOfUnchecked(final String s) {
        try {
            return valueOf(s);
        } catch (CheckedNumberFormatException e) {
            return ExceptionHandler.expectNetMask(s);
        }
    }
}