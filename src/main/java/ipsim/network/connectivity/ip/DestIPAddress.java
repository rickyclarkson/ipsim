package ipsim.network.connectivity.ip;

import ipsim.lang.Stringable;

public class DestIPAddress implements Stringable {
    private final IPAddress address;

    public DestIPAddress(final IPAddress address) {
        this.address = address;
    }

    public IPAddress getIPAddress() {
        return address;
    }

    @Override
    public String asString() {
        return address.asString();
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException();
    }
}