package ipsim.network.conformance;

import fpeas.function.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import ipsim.network.InvalidNetMaskException;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.Problem;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetMaskUtility;
import org.jetbrains.annotations.NotNull;

class ZeroSubnetMaskUsed implements Function<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult run(@NotNull final Network network)
	{
		final Function<Problem,CheckResult> func=new Function<Problem,CheckResult>()
		{
			@Override
            @NotNull
			public CheckResult run(@NotNull final Problem problem)
			{
				final int rawProblemNumber=problem.netBlock.networkNumber.rawValue;
				final int problemMaskPrefix;

				try
				{
					problemMaskPrefix=NetMaskUtility.getPrefixLength(problem.netBlock.netMask);
				}
				catch (final InvalidNetMaskException exception)
				{
					throw new RuntimeException("A problem has an invalid netmask, should be impossible - netmask is "+NetMask.asString(problem.netBlock.netMask.rawValue),exception);
				}

				return NonsensicalArrangement.customCheck(NetworkUtility.getAllCards,new Function<Card, Maybe<String>>()
				{
					@Override
                    @NotNull
					public Maybe<String> run(@NotNull final Card card)
					{
						final CardDrivers cardWithDrivers=card.withDrivers;
						if (cardWithDrivers==null)
							return MaybeUtility.nothing();

						try
						{
							final int rawNetworkNumber=cardWithDrivers.ipAddress.get().rawValue&cardWithDrivers.netMask.get().rawValue;
							final int cardNetMaskPrefix=NetMaskUtility.getPrefixLength(cardWithDrivers.netMask.get());

							final boolean equalNumbers=rawNetworkNumber==rawProblemNumber;

							if (equalNumbers&&problemMaskPrefix<cardNetMaskPrefix)
								return MaybeUtility.just("A subnet that uses an all-0s subnet number");

							if (problemMaskPrefix>=cardNetMaskPrefix)
								return MaybeUtility.just("A subnet mask that is equal to or shorter than the problem's netmask");

							if (equalNumbers)
								return MaybeUtility.just("No subnetting attempted");

							return MaybeUtility.nothing();
						}
						catch (InvalidNetMaskException exception)
						{
							return MaybeUtility.nothing();
						}
					}
				},NonsensicalArrangement.<Card>noErrors(),USUAL).run(network);

				/*
				 * for (final Card card: getAllCards(context)) try { final int rawNetworkNumber=card.getIPAddress().getRawValue()&card.getNetMask().getRawValue(); final int cardNetMaskPrefix=NetMaskUtility.getPrefixLength(card.getNetMask()); final boolean equalNumbers=equalT(rawNetworkNumber).run(rawProblemNumber); if (equalNumbers&&problemMaskPrefix<cardNetMaskPrefix) return FAILURE; //newCheckResult(USUAL,"A subnet that uses an all-0s subnet number"); if (problemMaskPrefix>=cardNetMaskPrefix) return FAILURE; //newCheckResult(USUAL,"A subnet mask that is equal to or shorter than the problem's netmask"); if (equalNumbers) return FAnewCheckResult(USUAL,"No subnetting attempted"); } catch (final NoDeviceDriversException exception) { } catch (final InvalidNetMaskException exception) { continue; } return newCheckResult(NONE,"No subnet that uses the whole network range");
				 */
			}
		};

		return CheckProblemUtility.check(network,func);
	}
}