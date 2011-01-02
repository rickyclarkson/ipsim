package ipsim.tests;

import com.rickyclarkson.testsuite.UnitTest;
import fj.F;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.hub.Hub;
import ipsim.network.connectivity.ping.PingResults;
import ipsim.network.ip.CheckedNumberFormatException;
import java.io.File;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import static ipsim.connectivity.PingTester.testPing;
import static ipsim.lang.Assertion.assertTrue;
import static ipsim.network.ip.IPAddressUtility.valueOf;
import static ipsim.util.Collections.all;

public class IncompleteArpTest implements UnitTest {
    @Override
    public boolean invoke() {
        final Network network = new Network();

        NetworkUtility.loadFromFile(network, new File("datafiles/unconnected/hubdisabled.ipsim"));

        try {
            if (!all(testPing(network, valueOf("146.87.1.1"), valueOf("146.87.1.2")), new F<List<PingResults>, Boolean>() {
                @Override
                @NotNull
                public Boolean f(@NotNull final List<PingResults> results) {
                    return 1 == results.size() && results.iterator().next().hostUnreachable();
                }
            }))
                return false;
        } catch (final CheckedNumberFormatException exception) {
            return false;
        }

        final Hub hub = NetworkUtility.getAllHubs(network).iterator().next();

        hub.setPower(true);

        try {
            for (final List<PingResults> results : testPing(network, valueOf("146.87.1.1"), valueOf("146.87.1.2"))) {
                assertTrue(1 == results.size());
                assertTrue(results.iterator().next().hostUnreachable());
            }
        } catch (final CheckedNumberFormatException exception) {
            return false;
        }

        return true;
    }

    public String toString() {
        return "IncompleteArpTest";
    }
}