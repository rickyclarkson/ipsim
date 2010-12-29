package ipsim.network.conformance;

import ipsim.network.connectivity.PacketSource;

import java.util.List;

public class CheckResult
{
	public final int deductions;
	public final List<String> summary;
	public final List<PacketSource> withWarnings;

	public CheckResult(final int deductions,final List<String> summary,final List<PacketSource> withWarnings,final List<PacketSource> withErrors)
	{
		this.deductions=deductions;
		this.summary=summary;
		this.withWarnings=withWarnings;
	}
}