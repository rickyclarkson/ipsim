package ipsim.gui.components;

import ipsim.network.Network;
import ipsim.network.conformance.CheckResult;

import java.util.List;

public interface WarningGenerator
{
	List<CheckResult> allChecks(Network network);
}