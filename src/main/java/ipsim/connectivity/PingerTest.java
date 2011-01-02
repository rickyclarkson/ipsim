package ipsim.connectivity;

import com.rickyclarkson.testsuite.UnitTest;
import fj.Effect;
import fj.F;
import ipsim.Globals;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ping.PingResults;
import ipsim.network.connectivity.ping.Pinger;
import ipsim.network.ip.CheckedNumberFormatException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import static ipsim.Caster.equalT;
import static ipsim.network.NetworkUtility.getComputersByIP;
import static ipsim.network.ip.IPAddressUtility.valueOf;
import static ipsim.util.Collections.all;

public class PingerTest implements UnitTest {
    @Override
    public boolean invoke() {
        final Network network = new Network();

        NetworkUtility.loadFromFile(network, new File("datafiles/unconnected/pingertest1.ipsim"), Effect.<IOException>throwRuntimeException());

        try {
            final IPAddress ip4_3 = valueOf("146.87.4.3");
            final IPAddress ip4_1 = valueOf("146.87.4.1");

            return all(getComputersByIP(network, valueOf("146.87.1.1")), new F<Computer, Boolean>() {
                @Override
                @NotNull
                public Boolean f(@NotNull final Computer computer) {
                    final List<PingResults> results = Pinger.ping(network, computer, new DestIPAddress(ip4_3), Globals.DEFAULT_TIME_TO_LIVE);

                    if (1 != results.size())
                        return false;

                    return all(results, new F<PingResults, Boolean>() {
                        @Override
                        @NotNull
                        public Boolean f(@NotNull final PingResults result) {
                            final boolean answer = result.hostUnreachable() && equalT(result.getReplyingHost().getIPAddress(), ip4_1);

                            if (!answer)
                                throw new RuntimeException(result.asString());

                            return answer;
                        }
                    });
                }
            });
        } catch (final CheckedNumberFormatException exception) {
            return false;
        }
    }

    public String toString() {
        return "PingerTest";
    }
}
