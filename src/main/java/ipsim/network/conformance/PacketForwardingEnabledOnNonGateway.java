package ipsim.network.conformance;

import fpeas.function.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.conformance.ConformanceTestsUtility.isARouter;
import static ipsim.network.conformance.NonsensicalArrangement.noErrors;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.computer.Computer;
import org.jetbrains.annotations.NotNull;

final class PacketForwardingEnabledOnNonGateway implements Function<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult run(@NotNull final Network network)
	{
		final Function<Computer, Maybe<String>> noErrors=noErrors(),warning=new Function<Computer, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> run(@NotNull final Computer computer)
			{
				return !isARouter().run(computer)&& computer.ipForwardingEnabled ? MaybeUtility.just("Computer that is not a gateway but has packet forwarding enabled") : MaybeUtility.<String>nothing();
			}
		};

		return NonsensicalArrangement.customCheck(getAllComputers,warning,noErrors,USUAL).run(network);
	}
}