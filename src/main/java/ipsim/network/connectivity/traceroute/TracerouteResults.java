package ipsim.network.connectivity.traceroute;

import ipsim.lang.Stringable;

public interface TracerouteResults extends Stringable {
    void add(final String object);

    int size();
}