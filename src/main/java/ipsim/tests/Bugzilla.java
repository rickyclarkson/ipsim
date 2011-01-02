package ipsim.tests;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.network.ProblemUtility;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetBlockUtility;

public class Bugzilla {
    public static final UnitTest bug18 = new UnitTest() {
        @Override
        public boolean invoke() {
            final String[] nets = {"192.168.1.0/24", "192.168.0.0/17", "192.168.0.0/15", "192.128.0.0/10", "192.0.0.0/8"};
            for (final String net : nets) {
                final NetBlock block = NetBlockUtility.createNetBlockOrThrowRuntimeException(net);
                if (!ProblemUtility.isReservedNetworkNumber(block))
                    return false;
            }

            final String[] allowed = {"192.128.0.0/11"};
            for (final String net : allowed) {
                final NetBlock block = NetBlockUtility.createNetBlockOrThrowRuntimeException(net);
                if (ProblemUtility.isReservedNetworkNumber(block))
                    return false;
            }

            return true;
        }

        public String toString() {
            return "Bugzilla bug 18";
        }
    };
}
