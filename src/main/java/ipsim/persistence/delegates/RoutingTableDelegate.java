package ipsim.persistence.delegates;

import fpeas.maybe.Maybe;
import static fpeas.maybe.MaybeUtility.nothing;
import fpeas.sideeffect.SideEffectUtility;
import static ipsim.lang.Assertion.assertNotNull;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.computer.RoutingTable;
import static ipsim.network.connectivity.computer.RoutingTableUtility.createRoutingTable;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import static ipsim.persistence.delegates.RouteDelegate.routeDelegate;
import ipsim.util.Collections;
import ipsim.Caster;
import org.w3c.dom.Node;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import java.util.List;

public final class RoutingTableDelegate
{
	public static SerialisationDelegate<RoutingTable> routingTableDelegate=new SerialisationDelegate<RoutingTable>()
	{
		@Override
        public void writeXML(final XMLSerialiser serialiser, final RoutingTable table)
		{
			int a=0;

			for (final Route entry : table.routes())
			{
				assertNotNull(entry);
				serialiser.writeObject(entry, "entry "+a, routeDelegate);
				a++;
			}
		}

		@Override
        public RoutingTable readXML(final XMLDeserialiser deserialiser, final Node node, final RoutingTable table)
		{
			final List<String> names=Collections.arrayList();

			names.addAll(asList(deserialiser.getObjectNames(node)));

			names.remove("computer");

			sort(names);
			final Maybe<Computer> nothing=nothing();

			for (final String name : names)
				table.add(nothing, deserialiser.readObject(node, name, routeDelegate, Caster.asFunction(Route.class)), SideEffectUtility.<IPAddress>throwRuntimeException());

			return table;
		}

		@Override
        public RoutingTable construct()
		{
			return createRoutingTable();
		}

		@Override
        public String getIdentifier()
		{
			return "ipsim.persistence.delegates.RoutingTableDelegate";
		}
	};
}