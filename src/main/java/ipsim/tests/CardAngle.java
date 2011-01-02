package ipsim.tests;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.awt.Point;
import ipsim.network.Network;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.ComputerFactory;

import static ipsim.gui.GetChildOffset.getChildOffset;
import static ipsim.gui.PositionUtility.setParent;

public class CardAngle {
    public static UnitTest testComputerWithTwoCards() {
        return new UnitTest() {
            @Override
            public boolean invoke() {
                final Network network = new Network();

                final Computer computer = ComputerFactory.newComputer(network, 100, 100);
                final Card card1 = network.cardFactory.f(new Point(150, 100));
                final Card card2 = network.cardFactory.f(new Point(100, 150));

                setParent(network, card1, 0, computer, 0);
                setParent(network, card2, 0, computer, 0);
                return getChildOffset(computer, card1).x != getChildOffset(computer, card2).x;
            }

            public String toString() {
                return "testComputerWithTwoCards";
            }
        };
    }
}