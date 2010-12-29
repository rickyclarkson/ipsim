package ipsim.network.ethernet;

import ipsim.network.connectivity.card.CardDrivers;

import java.util.Comparator;

public final class CardComparator implements Comparator<CardDrivers>
{
	@Override
    public int compare(final CardDrivers card1,final CardDrivers card2)
	{
		return card1.ethNumber-card2.ethNumber;
	}
}