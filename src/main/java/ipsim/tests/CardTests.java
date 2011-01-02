package ipsim.tests;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.awt.Point;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.ComputerFactory;

import static ipsim.gui.PositionUtility.setParent;

public class CardTests {
    public static UnitTest testDrivers() {
        return new UnitTest() {
            @Override
            public boolean invoke() {
                final Network network = new Network();

                final Card card = network.cardFactory.f(new Point(200, 200));
                final Computer computer = ComputerFactory.newComputer(network, 300, 300);
                setParent(network, card, 0, computer, 0);

                card.installDeviceDrivers(network);

                final String saved = NetworkUtility.saveToString(network);
                NetworkUtility.loadFromString(network, saved);
                return !NetworkUtility.getAllCardsWithDrivers(network).isEmpty();
            }

            public String toString() {
                return "CardTest.testDrivers";
            }
        };
    }
}

/*
<!DOCTYPE object [
<!ELEMENT object (object|attribute)*>
<!ATTLIST object
name CDATA #IMPLIED
id CDATA #REQUIRED
serialiser CDATA #REQUIRED
>
<!ELEMENT attribute EMPTY>
<!ATTLIST attribute
name CDATA #REQUIRED
value CDATA #REQUIRED
>
]>
<object name="network" serialiser="ipsim.persistence.delegates.NetworkDelegate" id="0">
	<attribute name="version" value="1.5"/>
	<object name="child 0" serialiser="ipsim.persistence.delegates.ComputerDelegate" id="1">
		<attribute name="ipForwardingEnabled" value="false"/>
		<object name="child 0" serialiser="ipsim.persistence.delegates.EthernetCardDelegate" id="2">
			<attribute name="ethNumber" value="0"/>
			<attribute name="angle" value="0"/>
			<object name="parent 0" serialiser="ipsim.persistence.delegates.ComputerDelegate" id="1"></object>
			<attribute name="ipAddress" value="0.0.0.0"/>
			<attribute name="netMask" value="0.0.0.0"/>
		</object>
		<object name="point 0" serialiser="ipsim.persistence.delegates.PointDelegate" id="3">
			<attribute name="x" value="300.0"/><attribute name="y" value="300.0"/>
		</object>
		<object name="routingTable" serialiser="ipsim.persistence.delegates.RoutingTableDelegate" id="4"></object>
	</object>
	<object name="log" serialiser="ipsim.persistence.delegates.LogDelegate" id="5"><object name="entry 0" serialiser="ipsim.persistence.delegates.DefaultCommandDelegate" id="6"><attribute name="description" value="Created an Ethernet card (at 200,200)."/></object><object name="entry 1" serialiser="ipsim.persistence.delegates.DefaultCommandDelegate" id="7"><attribute name="description" value="Connected an Ethernet card (at 200,200) to a computer (no ID, this is a bug) (at 300,300)."/></object><object name="entry 2" serialiser="ipsim.persistence.delegates.DefaultCommandDelegate" id="8"><attribute name="description" value="Installed the device drivers on an Ethernet card that is connected to a computer (no ID, this is a bug) (at 300,300)."/></object>
	</object>
</object>
*/