package ipsim.network.conformance;

import ipsim.network.connectivity.PacketSource;
import java.util.List;

import static ipsim.network.conformance.TypicalScores.NONE;
import static ipsim.util.Collections.arrayList;

public class CheckResultUtility {
    public static CheckResult fine() {
        final List<PacketSource> empty = arrayList();
        final List<String> empty2 = arrayList();
        return new CheckResult(NONE, empty2, empty, empty);
    }
}