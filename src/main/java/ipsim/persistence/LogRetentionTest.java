package ipsim.persistence;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.util.Collections;

import static ipsim.Caster.equalT;

public class LogRetentionTest implements UnitTest {
    @Override
    public boolean invoke() {
        Network network = new Network();
        network.log = Collections.add(network.log, "Sample Data");
        final String xml = NetworkUtility.saveToString(network);
        network = new Network();

        NetworkUtility.loadFromString(network, xml);

        return equalT("Sample Data", network.log.get(0));
    }

    public String toString() {
        return "Log retention test";
    }
}