package ipsim.network.conformance;

import fj.F;
import fj.data.Option;
import ipsim.network.InvalidNetMaskException;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.Problem;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetMaskUtility;
import org.jetbrains.annotations.NotNull;

import static ipsim.network.conformance.TypicalScores.USUAL;

class ZeroSubnetMaskUsed extends F<Network,CheckResult>
{
	@Override
    @NotNull
	public CheckResult f(@NotNull final Network network)
	{
		final F<Problem,CheckResult> func=new F<Problem,CheckResult>()
		{
			@Override
            @NotNull
			public CheckResult f(@NotNull final Problem problem)
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

				return NonsensicalArrangement.customCheck(NetworkUtility.getAllCards,new F<Card, Option<String>>()
				{
					@Override
                    @NotNull
					public Option<String> f(@NotNull final Card card)
					{
						final CardDrivers cardWithDrivers=card.withDrivers;
						if (cardWithDrivers==null)
							return Option.none();

						try
						{
							final int rawNetworkNumber=cardWithDrivers.ipAddress.get().rawValue&cardWithDrivers.netMask.get().rawValue;
							final int cardNetMaskPrefix=NetMaskUtility.getPrefixLength(cardWithDrivers.netMask.get());

							final boolean equalNumbers=rawNetworkNumber==rawProblemNumber;

							if (equalNumbers&&problemMaskPrefix<cardNetMaskPrefix)
								return Option.some("A subnet that uses an all-0s subnet number");

							if (problemMaskPrefix>=cardNetMaskPrefix)
								return Option.some("A subnet mask that is equal to or shorter than the problem's netmask");

							if (equalNumbers)
								return Option.some("No subnetting attempted");

							return Option.none();
						}
						catch (InvalidNetMaskException exception)
						{
							return Option.none();
						}
					}
				},NonsensicalArrangement.<Card>noErrors(),USUAL).f(network);

				/*
				 * for (final Card card: getAllCards(context)) try { final int rawNetworkNumber=card.getIPAddress().getRawValue()&card.getNetMask().getRawValue(); final int cardNetMaskPrefix=NetMaskUtility.getPrefixLength(card.getNetMask()); final boolean equalNumbers=equalT(rawNetworkNumber).run(rawProblemNumber); if (equalNumbers&&problemMaskPrefix<cardNetMaskPrefix) return FAILURE; //newCheckResult(USUAL,"A subnet that uses an all-0s subnet number"); if (problemMaskPrefix>=cardNetMaskPrefix) return FAILURE; //newCheckResult(USUAL,"A subnet mask that is equal to or shorter than the problem's netmask"); if (equalNumbers) return FAnewCheckResult(USUAL,"No subnetting attempted"); } catch (final NoDeviceDriversException exception) { } catch (final InvalidNetMaskException exception) { continue; } return newCheckResult(NONE,"No subnet that uses the whole network range");
				 */
			}
		};

		return CheckProblemUtility.check(network,func);
	}
}