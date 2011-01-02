package ipsim.connectivity.computer.arp.incoming;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.Caster;
import ipsim.awt.Point;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.OutgoingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketQueue;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketUtility2;
import ipsim.network.connectivity.arp.ArpPacket;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.ComputerFactory;
import ipsim.network.connectivity.ethernet.MacAddress;
import ipsim.network.connectivity.ip.IPAddress;

import static ipsim.gui.PositionUtility.setParent;

public class ComputerArpIncomingTest implements UnitTest {
    /**
     * Tests that when the Computer receives an ARP request (destination MAC address is zero), it sends an ARP reply.
     */
    @Override
    public boolean invoke() {
        final Network network = new Network();

        final Card card = network.cardFactory.f(new Point(0, 0));

        final Computer computer = ComputerFactory.newComputer(network, 0, 0);
        computer.computerID = network.generateComputerID();

        setParent(network, card, 0, computer, 0);

        card.installDeviceDrivers(network);

        final CardDrivers cardDrivers = card.withDrivers;

        cardDrivers.ipAddress.set(new IPAddress(10));

        final PacketQueue queue = network.packetQueue;

        final StringBuilder answer = new StringBuilder();

        computer.getOutgoingPacketListeners().add(new OutgoingPacketListener() {
            @Override
            public void packetOutgoing(final Packet packet, final PacketSource source) {
                if (PacketUtility2.isArpPacket(packet) && PacketSourceUtility.isComputer(source))
                    answer.append("Pass");
            }

            @Override
            public boolean canHandle(final Packet packet, final PacketSource source) {
                return true;
            }
        });

        final ArpPacket packet = new ArpPacket(new IPAddress(10), new MacAddress(0), new IPAddress(5), new MacAddress(6), new Object());
        queue.enqueueIncomingPacket(packet, card, computer);

        queue.processAll();

        return Caster.equalT(answer.toString(), "Pass");
    }

    public String toString() {
        return "ComputerArpIncomingTest";
    }
}