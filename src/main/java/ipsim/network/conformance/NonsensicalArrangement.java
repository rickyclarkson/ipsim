package ipsim.network.conformance;

import fpeas.function.Function;
import static fpeas.function.FunctionUtility.constant;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import fpeas.sideeffect.SideEffect;
import static ipsim.Caster.equalT;
import ipsim.network.Network;
import static ipsim.network.NetworkUtility.getAllCables;
import static ipsim.network.NetworkUtility.getAllCards;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.NetworkUtility.getAllHubs;
import static ipsim.network.conformance.TypicalScores.NONE;
import static ipsim.network.conformance.TypicalScores.USUAL;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;
import ipsim.network.ip.IPAddressUtility;
import static ipsim.util.Collections.arrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class NonsensicalArrangement
{
	static <T> Function<T, Maybe<String>> noErrors()
	{
		return constant(MaybeUtility.<String>nothing());
	}

	public static Function<Network, CheckResult> computerWithoutNetworkCard()
	{
		final Function<Computer, Maybe<String>> hasCard=new Function<Computer, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> run(@NotNull final Computer computer)
			{
				return computer.getCards().isEmpty() ? MaybeUtility.just("A computer without a network card") : MaybeUtility.<String>nothing();
			}
		};

		final Function<Computer, Maybe<String>> noErrors=noErrors();
		return customCheck(getAllComputers,hasCard,noErrors,USUAL);
	}

	public static Function<Network, CheckResult> cardWithoutCable()
	{
		final Function<Card, Maybe<String>> hasCable=new Function<Card, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> run(@NotNull final Card card)
			{
				return card.hasCable() ? MaybeUtility.<String>nothing() : MaybeUtility.just("Card without a cable");
			}
		};

		final Function<Card, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllCards,hasCable,noErrors,USUAL);
	}

	public static <T extends PacketSource> Function<Network,CheckResult> customCheck(final Function<Network,Iterable<T>> getCollection,final Function<T, Maybe<String>> warning,final Function<T,Maybe<String>> error,final int deductionsIfFound)
	{
		return new Function<Network,CheckResult>()
		{
			@Override
            @NotNull
			public CheckResult run(@NotNull final Network network)
			{
				final List<PacketSource> withWarnings=arrayList(),withErrors=arrayList();

				final List<String> warningSummary=arrayList(),errorSummary=arrayList();

				for (final T component: getCollection.run(network))
				{
					MaybeUtility.run(warning.run(component),new SideEffect<String>()
					{
						@Override
                        public void run(final String s)
						{
							withWarnings.add(component);
							warningSummary.add(s);
						}
					});

					MaybeUtility.run(error.run(component),new SideEffect<String>()
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

	public static Function<Network, CheckResult> cardWithZeroIP()
	{
		final Function<Card,Maybe<String>> zeroIPTest=new Function<Card,Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> run(@NotNull final Card card)
			{
				final CardDrivers cardWithDrivers=card.withDrivers;

				if (cardWithDrivers==null)
					return MaybeUtility.nothing();

				return equalT(IPAddressUtility.zero, cardWithDrivers.ipAddress.get()) ? MaybeUtility.just("A card with a 0.0.0.0 IP address") : MaybeUtility.<String>nothing();
			}
		};

		final Function<Card, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllCards,zeroIPTest,noErrors,USUAL);
	}

	public static Function<Network, CheckResult> cardWithoutDeviceDrivers()
	{
		final Function<Card, Maybe<String>> deviceDriverTest=new Function<Card, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> run(@NotNull final Card card)
			{
				return card.hasDeviceDrivers() ? MaybeUtility.<String>nothing() : MaybeUtility.just("Card with no device drivers");
			}
		};

		final Function<Card, Maybe<String>> noErrors=noErrors();
		return customCheck(getAllCards,deviceDriverTest,noErrors,USUAL);
	}

	public static Function<Network, CheckResult> cableWithEndsDisconnected()
	{
		return new Function<Network, CheckResult>()
		{
			@Override
            @NotNull
			public CheckResult run(@NotNull final Network network)
			{
				final Function<Cable, Maybe<String>> testCableEnds=new Function<Cable, Maybe<String>>()
				{
					@Override
                    @NotNull
					public Maybe<String> run(@NotNull final Cable cable)
					{
						return cable.getEnds(network).size()==2 ? MaybeUtility.<String>nothing() : MaybeUtility.just("A cable that has not got both ends connected to components");
					}
				};

				final Function<Cable, Maybe<String>> noErrors=noErrors();

				return customCheck(getAllCables(),testCableEnds,noErrors,USUAL).run(network);
			}
		};
	}

	public static Function<Network, CheckResult> hubWithNoCables()
	{
		final Function<Hub, Maybe<String>> testHub=new Function<Hub, Maybe<String>>()
		{
			@Override
            @NotNull
			public Maybe<String> run(@NotNull final Hub hub)
			{
				return hub.getCables().isEmpty() ? MaybeUtility.just("A hub that has no cables") : MaybeUtility.<String>nothing();
			}
		};

		final Function<Hub, Maybe<String>> noErrors=noErrors();

		return customCheck(getAllHubs,testHub,noErrors,USUAL);
	}
}