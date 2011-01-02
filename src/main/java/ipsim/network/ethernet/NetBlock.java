package ipsim.network.ethernet;

import ipsim.lang.Assertion;
import ipsim.lang.Stringable;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ip.IPAddressUtility;

import static ipsim.Caster.equalT;

public final class NetBlock implements Stringable {
    public final NetMask netMask;
    public final IPAddress networkNumber;

    public NetBlock(final IPAddress networkNumber, final NetMask netMask) {
        this.netMask = netMask;
        this.networkNumber = networkNumber;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof NetBlock))
            return false;

        final NetBlock netBlock = (NetBlock) other;
        final Boolean equalMasks = equalT(netBlock.netMask, netMask);
        final Boolean equalNumbers = equalT(netBlock.networkNumber, networkNumber);

        return equalMasks && equalNumbers;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public String asString() {
        return IPAddressUtility.toString(networkNumber.rawValue) + " netmask " + NetMask.asString(netMask.rawValue);
    }

    public boolean networkContains(final IPAddress ipAddress) {
        Assertion.assertNotNull(ipAddress);

        return (ipAddress.rawValue & netMask.rawValue) == networkNumber.rawValue;
    }
}