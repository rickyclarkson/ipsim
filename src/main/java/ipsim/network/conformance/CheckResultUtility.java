package ipsim.network.conformance;

import static ipsim.network.conformance.TypicalScores.NONE;
import ipsim.network.connectivity.PacketSource;
import static ipsim.util.Collections.arrayList;

import java.util.List;

public class CheckResultUtility
{
	public static CheckResult fine()
	{
		final List<PacketSource> empty=arrayList();
		final List<String> empty2=arrayList();
		return new CheckResult(NONE,empty2,empty,empty);
	}
}