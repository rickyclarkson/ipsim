package ipsim.tests;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.Caster;
import ipsim.connectivity.PingTester;
import ipsim.io.IOUtility;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.ip.CheckedNumberFormatException;
import java.io.File;
import java.net.MalformedURLException;

import static ipsim.network.ip.IPAddressUtility.valueOf;

public class InfiniteLoopBug implements UnitTest {
    @Override
    public boolean invoke() {
        final Network context = new Network();

        try {
            NetworkUtility.loadFromString(context, Caster.asFirst(IOUtility.readWholeResource(new File("datafiles/fullyconnected/101.ipsim").toURI().toURL())));
        } catch (final MalformedURLException exception) {
            return false;
        }

        try {
            PingTester.testPing(context, valueOf("146.87.1.1"), valueOf("146.87.1.255"));
        } catch (final CheckedNumberFormatException exception) {
            return false;
        }

        return true;
    }

    public String toString() {
        return "InfiniteLoopBug";
    }
}
