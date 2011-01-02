package ipsim.network.connectivity.traceroute;

import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ping.PingResults;
import ipsim.network.connectivity.ping.Pinger;
import java.util.List;

public class Traceroute {
    private Traceroute() {
    }

    public static TracerouteResults trace(final Network network, final Computer computer, final DestIPAddress destIP, final int maxTTL) {
        final TracerouteResults results = TracerouteResultsUtility.newTracerouteResults();

        boolean stop = false;

        for (int ttl = 1; ttl <= maxTTL && !stop; ttl++) {
            final List<PingResults> pingResults = Pinger.ping(network, computer, destIP, ttl);

            for (final PingResults result : pingResults) {
                if (result.ttlExpired() || result.pingReplyReceived())
                    results.add(ttl + ": " + result.getReplyingHost().asString());
                else
                    results.add(ttl + ": " + result.asString());

                if (!result.timedOut() && !result.ttlExpired())
                    stop = true;
            }
        }

        return results;
    }
}