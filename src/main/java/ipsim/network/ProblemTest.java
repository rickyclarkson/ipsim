package ipsim.network;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;

import static ipsim.Caster.equalT;

public class ProblemTest {
    public static UnitTest instance() {
        return new UnitTest() {
            @Override
            public boolean invoke() {
                return test1() && testInvalidNetMaskRejection();
            }

            private boolean testInvalidNetMaskRejection() {
                return invalidNetMask("255.255.22.0") && invalidNetMask("255.0.0.0");
            }

            private boolean invalidNetMask(final String mask) {
                try {
                    return new ProblemBuilder().f(5).left().value().f(new NetBlock(IPAddressUtility.valueOf("146.87.0.0"), NetMaskUtility.valueOf(mask))).isRight();
                } catch (final CheckedNumberFormatException exception) {
                    throw new RuntimeException(exception);
                }
            }

            public boolean test1() {
                final IPAddress address = new IPAddress(221 << 24);
                final NetMask mask = NetMaskUtility.getNetMask(255 << 24);

                final NetBlock netBlock = new NetBlock(address, mask);

                try {
                    return equalT(netBlock.networkNumber, IPAddressUtility.valueOf("221.0.0.0"));
                } catch (final CheckedNumberFormatException exception) {
                    return false;
                }
            }

            public String toString() {
                return "ProblemTest";
            }
        };
    }
}