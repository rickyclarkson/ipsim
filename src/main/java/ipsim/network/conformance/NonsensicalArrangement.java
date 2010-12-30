package ipsim.network.conformance;

import fj.F;
import fj.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import fpeas.sideeffect.SideEffect;
import ipsim.network.Network;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;
import ipsim.network.ip.IPAddressUtility;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import static ipsim.Caster.equalT;
import static ipsim.network.NetworkUtility.getAllCables;
import static ipsim.network.NetworkUtility.getAllCards;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.NetworkUtility.getAllHubs;
import static ipsim.network.conformance.TypicalScores.NONE;
import static ipsim.network.conformance.TypicalScores.USUAL;
import static ipsim.util.Collections.arrayList;

class NonsensicalArrangement
{
	static <T> F<T, Maybe<String>> noErrors()
	{
		return Function.constant(MaybeUtility.<String>nothing());
	}

	public static F<Network, CheckResult> computerWithoutNetworkCard()
	{
		final F<Computer, Maybe<String>> hasCard=new F<Computer, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> f(@NotNull final Computer computer)
			{
				return computer.getCards().isEmpty() ? MaybeUtility.just("A computer without a network card") : MaybeUtility.<String>nothing();
			}
		};

		final F<Computer, Maybe<String>> noErrors=noErrors();
		return customCheck(getAllComputers,hasCard,noErrors,USUAL);
	}

	public static F<Network, CheckResult> cardWithoutCable()
	{
		final F<Card, Maybe<String>> hasCable=new F<Card, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> f(@NotNull final Card card)
			{
				return card.hasCable() ? MaybeUtility.<String>nothing() : MaybeUtility.just("Card without a cable");
			}
		};

		final F<Card, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllCards,hasCable,noErrors,USUAL);
	}

	public static <T extends PacketSource> F<Network,CheckResult> customCheck(final F<Network,Iterable<T>> getCollection,final F<T, Maybe<String>> warning,final F<T,Maybe<String>> error,final int deductionsIfFound)
	{
		return new F<Network,CheckResult>()
		{
			@Override
            @NotNull
			public CheckResult f(@NotNull final Network network)
			{
				final List<PacketSource> withWarnings=arrayList(),withErrors=arrayList();

				final List<String> warningSummary=arrayList(),errorSummary=arrayList();

				for (final T component: getCollection.f(network))
				{
					MaybeUtility.run(warning.f(component),new SideEffect<String>()
					{
						@Override
                        public void run(final String s)
						{
							withWarnings.add(component);
							warningSummary.add(s);
						}
					});

					MaybeUtility.run(error.f(component),new SideEffect<String>()
					{
						@Override
                        public void run(final String s)
						{
							withErrors.add(component);
							errorSummary.add(s);
						}
					});
				}

				final boolean found=!(0==withWarnings.size()+withErrors.size());

				final int deductions=found ? deductionsIfFound : NONE;

				warningSummary.addAll(errorSummary);
				return new CheckResult(deductions, warningSummary, withWarnings, withErrors);
			}
		};
	}

	public static F<Network, CheckResult> cardWithZeroIP()
	{
		final F<Card,Maybe<String>> zeroIPTest=new F<Card,Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> f(@NotNull final Card card)
			{
				final CardDrivers cardWithDrivers=card.withDrivers;

				if (cardWithDrivers==null)
					return MaybeUtility.nothing();

				return equalT(IPAddressUtility.zero, cardWithDrivers.ipAddress.get()) ? MaybeUtility.just("A card with a 0.0.0.0 IP address") : MaybeUtility.<String>nothing();
			}
		};

		final F<Card, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllCards,zeroIPTest,noErrors,USUAL);
	}

	public static F<Network, CheckResult> cardWithoutDeviceDrivers()
	{
		final F<Card, Maybe<String>> deviceDriverTest=new F<Card, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> f(@NotNull final Card card)
			{
				return card.hasDeviceDrivers() ? MaybeUtility.<String>nothing() : MaybeUtility.just("Card with no device drivers");
			}
		};

		final F<Card, Maybe<String>> noErrors=noErrors();
		return customCheck(getAllCards,deviceDriverTest,noErrors,USUAL);
	}

	public static F<Network, CheckResult> cableWithEndsDisconnected()
	{
		return new F<Network, CheckResult>()
		{
			@Override
            @NotNull
			public CheckResult f(@NotNull final Network network)
			{
				final F<Cable, Maybe<String>> testCableEnds=new F<Cable, Maybe<String>>()
				{
					@Override
                    @NotNull
					public Maybe<String> f(@NotNull final Cable cable)
					{
						return cable.getEnds(network).size()==2 ? MaybeUtility.<String>nothing() : MaybeUtility.just("A cable that has not got both ends connected to components");
					}
				};

				final F<Cable, Maybe<String>> noErrors=noErrors();

				return customCheck(getAllCables(),testCableEnds,noErrors,USUAL).f(network);
			}
		};
	}

	public static F<Network, CheckResult> hubWithNoCables()
	{
		final F<Hub, Maybe<String>> testHub=new F<Hub, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> f(@NotNull final Hub hub)
			{
				return hub.getCables().isEmpty() ? MaybeUtility.just("A hub that has no cables") : MaybeUtility.<String>nothing();
			}
		};

		final F<Hub, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllHubs,testHub,noErrors,USUAL);
	}
}