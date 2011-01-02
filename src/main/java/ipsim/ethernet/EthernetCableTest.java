package ipsim.ethernet;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.network.Network;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.ComputerFactory;

import static ipsim.gui.PositionUtility.setParent;

public final class EthernetCableTest implements UnitTest {
    @Override
    public boolean invoke() {
        final Network network = new Network();

        final Computer computer = ComputerFactory.newComputer(network, 0, 0);
        computer.computerID = network.generateComputerID();

        final Cable cable = network.cableFactory.newCable(0, 0, 0 + 50, 0);

        try {
            setParent(network, cable, 0, computer, 0);

            return false;
        } catch (final IllegalStateException exception) {
            return true;
        }
    }

    public String toString() {
        return "EthernetCableTest";
    }
}