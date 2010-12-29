package ipsim.network.ethernet;

import fpeas.predicate.Predicate;
import ipsim.lang.Stringable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.card.NoDeviceDriversException;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import static ipsim.network.ethernet.CardUtility.getNetBlock;
import ipsim.util.Collections;
import static ipsim.util.Collections.arrayList;
import static ipsim.util.Collections.asString;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import static java.util.Collections.sort;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class ComputerUtility
{
	/**
	 * Flawed because it only returns one card if there are more than one.
	 */
	@Nullable
	public static CardDrivers getCardFor(final Computer computer, final Route route)
	{
		final Iterable<CardDrivers> result=Collections.only(cardsWithDrivers(computer), new Predicate<CardDrivers>()
		{
			@Override
            public boolean invoke(final CardDrivers card)
			{
				final NetBlock netBlock=CardUtility.getNetBlock(card);

				return !(0==netBlock.networkNumber.rawValue) && netBlock.networkContains(route.gateway);
			}
		});

		final Iterator<CardDrivers> iterator=result.iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}

	public static Iterable<CardDrivers> getSortedCards(final Computer computer)
	{
		final List<CardDrivers> list=new ArrayList<CardDrivers>();

		for (final Card card: computer.getCards())
		{
			final CardDrivers withDrivers=card.withDrivers;

			if (withDrivers==null)
				continue;

			list.add(card.withDrivers);
		}

		sort(list, new CardComparator());

		return list;
	}

	public static String ipAddressesToString(final Computer computer)
	{
		final List<CardDrivers> set=cardsWithDrivers(computer);

		final Collection<Stringable> strings=new HashSet<Stringable>();

		final boolean[] foundOneNonZero={false};

		for (final CardDrivers card: set)
		{
			final NetBlock netBlock=CardUtility.getNetBlock(card);
			if (!(0==netBlock.networkNumber.rawValue) || !(0==netBlock.netMask.rawValue))
			foundOneNonZero[0]=true;

			final String string=card.ipAddress.get().asString()+'/'+NetMask.asCustomString(card.netMask.get());

			strings.add(new Stringable()
			{
				@Override
                public String asString()
				{
					return string;
				}
			});
		}

		if (!foundOneNonZero[0])
			return "";

		return asString(strings);
	}

	public static boolean isLocallyReachable(final Computer computer, final IPAddress ipAddress)
	{
		return Collections.any(computer.getCards(), new Predicate<Card>()
		{
			@Override
            public boolean invoke(final Card card)
			{
				try
				{
					return getNetBlock(card).networkContains(ipAddress);
				}
				catch (final NoDeviceDriversException exception)
				{
					return false;
				}
			}
		});
	}

	public static Collection<IPAddress> getIPAddresses(final Computer computer)
	{
		final Collection<IPAddress> set=new HashSet<IPAddress>();

		for (final CardDrivers card: cardsWithDrivers(computer))
			set.add(card.ipAddress.get());

		return set;
	}

	public static CardDrivers getEth(final Computer computer, final int cardNo)
	{
		final Iterable<CardDrivers> cards=getSortedCards(computer);

		for (final CardDrivers card : cards)
			if (cardNo==card.ethNumber)
				return card;

		throw new IllegalArgumentException("There is no installed card "+cardNo);
	}

	@Nullable
	public static IPAddress getTheFirstIPAddressYouCanFind(final Computer computer)
	{
		final Iterator<CardDrivers> iterator=cardsWithDrivers(computer).iterator();
		if (iterator.hasNext())
			return iterator.next().ipAddress.get();

		return null;
	}

	public static List<CardDrivers> cardsWithDrivers(final Computer computer)
	{
		final List<Card> cards=computer.getCards();
		final List<CardDrivers> results=arrayList();

		for (final Card card: cards)
		{
			final CardDrivers withDrivers=card.withDrivers;

			if (withDrivers==null)
				continue;

			results.add(withDrivers);
		}

		return results;
	}
}