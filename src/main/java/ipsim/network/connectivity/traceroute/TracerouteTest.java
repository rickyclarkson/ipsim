package ipsim.network.connectivity.traceroute;

import com.rickyclarkson.testsuite.UnitTest;
import fj.F;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;
import java.io.File;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.NetworkUtility.getComputersByIP;
import static ipsim.network.connectivity.traceroute.Traceroute.trace;
import static ipsim.network.ip.IPAddressUtility.valueOf;
import static ipsim.util.Collections.all;

public class TracerouteTest implements UnitTest {
    @Override
    public boolean invoke() {
        final Network network = new Network();

        NetworkUtility.loadFromFile(network, new File("datafiles/fullyconnected/1.6.ipsim"));

        final IPAddress ipAddress;
        try {
            ipAddress = IPAddressUtility.valueOf("146.87.1.1");
        } catch (CheckedNumberFormatException exception1) {
            return false;
        }

        return all(getComputersByIP(network, ipAddress), new F<Computer, Boolean>() {
            @Override
            @NotNull
            public Boolean f(@NotNull final Computer computer) {
                final TracerouteResults results;
                try {
                    results = trace(network, computer, new DestIPAddress(valueOf("146.87.4.3")), 30);
                } catch (final CheckedNumberFormatException exception) {
                    throw new RuntimeException(exception);
                }

                return results.asString().startsWith("1: 146.87.1.3\n2: 146.87.2.3\n3: 146.87.4.3");
            }
        });
    }

    public String toString() {
        return "TracerouteTest";
    }
}