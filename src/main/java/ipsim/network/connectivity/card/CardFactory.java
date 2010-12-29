package ipsim.network.connectivity.card;


import ipsim.awt.Point;
import static ipsim.gui.PositionUtility.setParent;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;

public class CardFactory
{
	private CardFactory()
	{
	}

	public static Card newCardConnectedTo(final Network network, final Computer computer, final int x, final int y)
	{
		final Card card=network.cardFactory.run(new Point(x, y));
		setParent(network,card,0,computer,0);
		return card;
	}
}