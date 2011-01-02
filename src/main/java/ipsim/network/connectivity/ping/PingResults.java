package ipsim.network.connectivity.ping;

import ipsim.lang.Stringable;
import ipsim.network.connectivity.ip.SourceIPAddress;

public interface PingResults extends Stringable {
    boolean pingReplyReceived();

    boolean hostUnreachable();

    SourceIPAddress getReplyingHost();

    boolean ttlExpired();

    boolean timedOut();
}