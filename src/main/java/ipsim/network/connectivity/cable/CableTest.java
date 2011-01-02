package ipsim.network.connectivity.cable;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.awt.Point;
import ipsim.network.Network;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.hub.Hub;
import ipsim.network.connectivity.hub.HubFactory;

import static ipsim.gui.PositionUtility.getPosition;
import static ipsim.gui.PositionUtility.numPositions;
import static ipsim.gui.PositionUtility.setParent;
import static ipsim.lang.Assertion.assertTrue;

public class CableTest {
    public static UnitTest testCable() {
        return new UnitTest() {
            @Override
            public boolean invoke() {
                final Network network = new Network();
                final Card card = network.cardFactory.f(new Point(0, 0));
                final Hub hub = HubFactory.newHub(network, 100, 100);
                final Cable cable = network.cableFactory.newCable(50, 50, 50 + 50, 50);
                final Card card2 = network.cardFactory.f(new Point(200, 200));

                setParent(network, cable, 0, hub, 0);
                setParent(network, cable, 1, card, 0);

                final boolean either1 = cable.canTransferPackets(network);

                setParent(network, cable, 0, card2, 0);
                final boolean either2 = cable.canTransferPackets(network);

                return either1 && !either2;
            }

            public String toString() {
                return "CableTest";
            }
        };
    }

    public static final UnitTest testCableWithNoParents = new UnitTest() {
        @Override
        public boolean invoke() {
            final Network network = new Network();
            final Cable cable = network.cableFactory.newCable(5, 5, 10, 10);

            for (int a = 0; a < numPositions(cable); a++)
                assertTrue(getPosition(network, cable, a).x == 5 + a * 5);

            return true;
        }

        public String toString() {
            return "testCableWithOneParent";
        }
    };
}