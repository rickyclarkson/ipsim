package ipsim.network.conformance;

import fj.F;
import fj.Function;
import fj.data.Option;
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
	static <T> F<T, Option<String>> noErrors()
	{
		return Function.constant(Option.<String>none());
	}

	public static F<Network, CheckResult> computerWithoutNetworkCard()
	{
		final F<Computer, Option<String>> hasCard=new F<Computer, Option<String>>()
		{
			@Override
            @NotNull
			public Option<String> f(@NotNull final Computer computer)
			{
				return computer.getCards().isEmpty() ? Option.some("A computer without a network card") : Option.<String>none();
			}
		};

		final F<Computer, Option<String>> noErrors=noErrors();
		return customCheck(getAllComputers,hasCard,noErrors,USUAL);
	}

	public static F<Network, CheckResult> cardWithoutCable()
	{
		final F<Card, Option<String>> hasCable=new F<Card, Option<String>>()
		{
			@Override
            @NotNull
			public Option<String> f(@NotNull final Card card)
			{
				return card.hasCable() ? Option.<String>none() : Option.some("Card without a cable");
			}
		};

		final F<Card, Option<String>> noErrors=noErrors();

		return customCheck(getAllCards,hasCable,noErrors,USUAL);
	}

	public static <T extends PacketSource> F<Network,CheckResult> customCheck(final F<Network,Iterable<T>> getCollection,final F<T, Option<String>> warning,final F<T,Option<String>> error,final int deductionsIfFound)
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
					for (String s: warning.f(component)) {
                        withWarnings.add(component);
                        warningSummary.add(s);
                    }

					for (String s: error.f(component)) {
                        withErrors.add(component);
                        errorSummary.add(s);
					}
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
		final F<Card,Option<String>> zeroIPTest=new F<Card,Option<String>>()
		{
			@Override
            @NotNull
			public Option<String> f(@NotNull final Card card)
			{
				final CardDrivers cardWithDrivers=card.withDrivers;

				if (cardWithDrivers==null)
					return Option.none();

				return equalT(IPAddressUtility.zero, cardWithDrivers.ipAddress.get()) ? Option.some("A card with a 0.0.0.0 IP address") : Option.<String>none();
			}
		};

		final F<Card, Option<String>> noErrors=noErrors();

		return customCheck(getAllCards,zeroIPTest,noErrors,USUAL);
	}

	public static F<Network, CheckResult> cardWithoutDeviceDrivers()
	{
		final F<Card, Option<String>> deviceDriverTest=new F<Card, Option<String>>()
		{
			@Override
            @NotNull
			public Option<String> f(@NotNull final Card card)
			{
				return card.hasDeviceDrivers() ? Option.<String>none() : Option.some("Card with no device drivers");
			}
		};

		final F<Card, Option<String>> noErrors=noErrors();
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
				final F<Cable, Option<String>> testCableEnds=new F<Cable, Option<String>>()
				{
					@Override
                    @NotNull
					public Option<String> f(@NotNull final Cable cable)
					{
						return cable.getEnds(network).size()==2 ? Option.<String>none() : Option.some("A cable that has not got both ends connected to components");
					}
				};

				final F<Cable, Option<String>> noErrors=noErrors();

				return customCheck(getAllCables(),testCableEnds,noErrors,USUAL).f(network);
			}
		};
	}

	public static F<Network, CheckResult> hubWithNoCables()
	{
		final F<Hub, Option<String>> testHub=new F<Hub, Option<String>>()
		{
			@Override
            @NotNull
			public Option<String> f(@NotNull final Hub hub)
			{
				return hub.getCables().isEmpty() ? Option.some("A hub that has no cables") : Option.<String>none();
			}
		};

		final F<Hub, Option<String>> noErrors=noErrors();

		return customCheck(getAllHubs,testHub,noErrors,USUAL);
	}
}