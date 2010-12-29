package ipsim.persistence.delegates;

import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;
import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import static ipsim.persistence.delegates.NetBlockDelegate.netBlockDelegate;
import ipsim.Caster;
import org.w3c.dom.Node;

public final class RouteDelegate
{
	public static SerialisationDelegate<Route> routeDelegate=new SerialisationDelegate<Route>()
	{
		@Override
        public void writeXML(final XMLSerialiser serialiser, final Route route)
		{
			serialiser.writeObject(route.block, "destination", netBlockDelegate);
			serialiser.writeAttribute("gateway", IPAddressUtility.toString(route.gateway.rawValue));
		}

		@Override
        public Route readXML(final XMLDeserialiser deserialiser, final Node node, final Route object)
		{
			final NetBlock destination=deserialiser.readObject(node, "destination", netBlockDelegate, Caster.asFunction(NetBlock.class));
			final IPAddress gateway;
			try
			{
				gateway=IPAddressUtility.valueOf(deserialiser.readAttribute(node, "gateway"));
			}
			catch (final CheckedNumberFormatException exception)
			{
				throw new RuntimeException(exception);
			}

			return new Route(destination, gateway);
		}

		@Override
        public Route construct()
		{
			return null;
		}

		@Override
        public String getIdentifier()
		{
			return "ipsim.persistence.delegates.RoutingTableEntryDelegate";
		}
	};
}