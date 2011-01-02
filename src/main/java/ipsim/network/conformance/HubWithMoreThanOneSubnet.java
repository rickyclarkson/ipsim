package ipsim.network.conformance;

import com.rickyclarkson.testsuite.UnitTest;
import fj.F;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.conformance.ConformanceTests.ResultsAndSummaryAndPercent;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.hub.Hub;
import ipsim.network.ethernet.CardUtility;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.OnlyOneEndConnectedException;
import ipsim.util.Collections;
import java.io.File;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ipsim.Caster.equalT;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asCard;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.isHub;
import static ipsim.network.NetworkUtility.loadFromFile;
import static ipsim.network.ethernet.CableUtility.getOtherEnd;
import static ipsim.util.Collections.arrayList;

public class HubWithMoreThanOneSubnet extends F<Network, CheckResult> {
    public static final String errorMessage = "Hub with more than one subnet connected to it";

    @Override
    @NotNull
    public CheckResult f(@NotNull final Network network) {
        final List<PacketSource> empty = arrayList();
        final List<PacketSource> warnings = arrayList();

        for (final Hub hub : NetworkUtility.getAllHubs(network)) {
            NetBlock netBlock = null;

            for (final Cable cable : hub.getCables()) {
                @Nullable final Card maybeCard;
                try {
                    maybeCard = asCard(getOtherEnd(network, cable, hub));
                } catch (final OnlyOneEndConnectedException exception) {
                    continue;
                }

                if (maybeCard == null)
                    continue;

                @Nullable final CardDrivers cardWithDrivers = maybeCard.withDrivers;

                if (cardWithDrivers == null || cardWithDrivers.ipAddress.get().rawValue == 0)
                    continue;

                final NetBlock temp = CardUtility.getNetBlock(cardWithDrivers);

                if (netBlock == null)
                    netBlock = temp;
                else if (!equalT(netBlock, temp))
                    warnings.add(hub);
            }
        }

        if (warnings.isEmpty())
            return CheckResultUtility.fine();

        return new CheckResult(TypicalScores.SEVERE, Collections.asList(errorMessage), warnings, empty);
    }

    public static UnitTest testFalsePositiveWithZeroIP() {
        return new UnitTest() {
            @Override
            public boolean invoke() {
                final Network network = new Network();
                loadFromFile(network, new File("datafiles/unconnected/hubwithmorethanonesubnet-zerotest.ipsim"));

                final ResultsAndSummaryAndPercent allChecks = ConformanceTests.allChecks(network);

                final List<CheckResult> results = allChecks.results;

                return !Collections.any(results, new F<CheckResult, Boolean>() {
                    @Override
                    public Boolean f(final CheckResult checkResult) {
                        return Collections.any(checkResult.withWarnings, isHub);
                    }
                });
            }

            public String toString() {
                return "testFalsePositiveWithZeroIP";
            }
        };
    }
}