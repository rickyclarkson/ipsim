package ipsim.network.ethernet;

import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.card.NoDeviceDriversException;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;

public final class CardUtility {
    public static IPAddress getBroadcastAddress(final CardDrivers card) {
        return NetBlockUtility.getBroadcastAddress(getNetBlock(card));
    }

    public static boolean isOnSameSubnet(final Card card, final IPAddress sourceIPAddress) {
        final NetBlock block;
        try {
            block = getNetBlock(card);
        } catch (final NoDeviceDriversException exception) {
            throw new RuntimeException(exception);
        }

        return block.networkContains(sourceIPAddress);
    }

    public static NetBlock getNetBlock(final Card card) throws NoDeviceDriversException {
        if (!card.hasDeviceDrivers())
            throw new NoDeviceDriversException("Blah");

        final CardDrivers cardWithDrivers = card.withDrivers;

        int rawIP = cardWithDrivers.ipAddress.get().rawValue;
        final NetMask netMask = cardWithDrivers.netMask.get();

        rawIP &= netMask.rawValue;

        return new NetBlock(new IPAddress(rawIP), netMask);
    }

    public static NetBlock getNetBlock(final CardDrivers cardWithDrivers) {
        int rawIP = cardWithDrivers.ipAddress.get().rawValue;
        final NetMask netMask = cardWithDrivers.netMask.get();

        rawIP &= netMask.rawValue;

        return new NetBlock(new IPAddress(rawIP), netMask);
    }
}