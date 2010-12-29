package ipsim.persistence.delegates;

import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.IPAddressUtility;
import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import org.w3c.dom.Node;

public final class NetBlockDelegate
{
	public static SerialisationDelegate<NetBlock> netBlockDelegate=new SerialisationDelegate<NetBlock>()
	{
		@Override
        public void writeXML(final XMLSerialiser serialiser, final NetBlock object)
		{
			serialiser.writeAttribute("netmask", NetMask.asString(object.netMask.rawValue));

			serialiser.writeAttribute("networkNumber", IPAddressUtility.toString(object.networkNumber.rawValue));
		}

		@Override
        public NetBlock readXML(final XMLDeserialiser deserialiser, final Node node, final NetBlock serialisable)
		{
			final IPAddress netNum=IPAddressUtility.valueOfUnchecked(deserialiser.readAttribute(node, "networkNumber"));
			final NetMask netMask=NetMaskUtility.valueOfUnchecked(deserialiser.readAttribute(node, "netmask"));

			return new NetBlock(netNum, netMask);
		}

		@Override
        public NetBlock construct()
		{
			return null;
		}

		public boolean canHandle(final NetBlock object)
		{
			return true;
		}

		@Override
        public String getIdentifier()
		{
			return "ipsim.persistence.delegates.NetBlockDelegate";
		}
	};
}