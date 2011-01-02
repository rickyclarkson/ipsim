package ipsim.network.connectivity.card;


import ipsim.awt.Point;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;

import static ipsim.gui.PositionUtility.setParent;

public class CardFactory {
    private CardFactory() {
    }

    public static Card newCardConnectedTo(final Network network, final Computer computer, final int x, final int y) {
        final Card card = network.cardFactory.f(new Point(x, y));
        setParent(network, card, 0, computer, 0);
        return card;
    }
}