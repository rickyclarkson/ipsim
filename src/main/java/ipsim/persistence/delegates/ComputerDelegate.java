package ipsim.persistence.delegates;

import com.rickyclarkson.testsuite.UnitTest;
import com.rickyclarkson.xml.DOMSimple;
import fpeas.maybe.MaybeUtility;
import fpeas.sideeffect.SideEffectUtility;
import ipsim.Caster;
import static ipsim.Caster.asFunction;
import ipsim.ExceptionHandler;
import ipsim.awt.Point;
import ipsim.gui.PositionUtility;
import ipsim.lang.Stringable;
import ipsim.network.Network;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.ComputerFactory;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.computer.RoutingTable;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLDeserialiserUtility;
import ipsim.persistence.XMLSerialiser;
import static ipsim.util.Collections.mapWith;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.StringWriter;

public class ComputerDelegate
{
	public static final UnitTest testSerialisationOfForwarding=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final Computer computer=new Computer();
			computer.ipForwardingEnabled=true;

			final StringWriter stringWriter=new StringWriter();

			final Network network=new Network();
			PositionUtility.setPosition(network, computer, mapWith(0, new Point(100, 100)));

			final XMLSerialiser serialiser=new XMLSerialiser(stringWriter);

			serialiser.writeObject(computer, "computer", computerDelegate(network));

			final Computer another=XMLDeserialiserUtility.createXMLDeserialiser(new DOMSimple(ExceptionHandler.<String>impossibleRef(), ExceptionHandler.<Element>impossibleRef()),stringWriter.getBuffer().toString()).readObject(computerDelegate(network), asFunction(Computer.class));

			return another.ipForwardingEnabled;
		}

		@Override
		public String toString()
		{
			return "testSerialisationOfForwarding";
		}
	};

	public static SerialisationDelegate<Computer> computerDelegate(final Network network)
	{
		return new SerialisationDelegate<Computer>()
		{
			@Override
            public void writeXML(final XMLSerialiser serialiser, final Computer computer)
			{
				serialiser.writeAttribute("ipForwardingEnabled", String.valueOf(computer.ipForwardingEnabled));
				final Stringable computerId=computer.computerID;

				if (computerId!=null)
					serialiser.writeAttribute("computerId", computerId.asString());

				DelegateHelper.writePositions(network, serialiser, computer);

				serialiser.writeObject(computer.routingTable, "routingTable", RoutingTableDelegate.routingTableDelegate);
			}

			@Override
            public Computer readXML(final XMLDeserialiser deserialiser, final Node node, final Computer computer)
			{
				computer.ipForwardingEnabled=Boolean.valueOf(Caster.asNotNull(deserialiser.readAttribute(node, "ipForwardingEnabled")));

				@Nullable
				final String idString=deserialiser.readAttribute(node, "computerId");

				computer.computerID=idString==null ? network.generateComputerID() : network.createComputerID(idString);

				DelegateHelper.readPositions(network, deserialiser, node, computer);

				final RoutingTable routingTable=deserialiser.readObject(node, "routingTable",RoutingTableDelegate.routingTableDelegate, asFunction(RoutingTable.class));
				final RoutingTable originalRoutingTable=computer.routingTable;

				for (final Route route : routingTable.routes())
					originalRoutingTable.add(MaybeUtility.<Computer>nothing(), route, SideEffectUtility.<IPAddress>throwRuntimeException());

				return computer;
			}

			@Override
            public Computer construct()
			{
				return ComputerFactory.newComputer(network, 0, 0);
			}

			@Override
            public String getIdentifier()
			{
				return "ipsim.persistence.delegates.ComputerDelegate";
			}
		};
	}
}
