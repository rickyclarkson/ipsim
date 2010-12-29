package ipsim.persistence.delegates;

import ipsim.awt.PointUtility;
import static ipsim.ExceptionHandler.impossible;
import ipsim.network.Network;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;
import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

public final class EthernetCardDelegate
{
	public static SerialisationDelegate<Card> cardDelegate(final Network network)
	{
		return new SerialisationDelegate<Card>()
		{
			@Override
            public void writeXML(final XMLSerialiser serialiser, final Card card)
			{
				@Nullable
				final CardDrivers drivers=card.withDrivers;

				if (drivers!=null)
				{
					serialiser.writeAttribute("ethNumber", String.valueOf(drivers.ethNumber));
					serialiser.writeAttribute("ipAddress", IPAddressUtility.toString(card.withDrivers.ipAddress.get().rawValue));
					serialiser.writeAttribute("netMask", NetMask.asString(card.withDrivers.netMask.get().rawValue));
				}

				DelegateHelper.writePositions(network, serialiser, card);
			}

			@Override
            public Card readXML(final XMLDeserialiser deserialiser, final Node node, final Card card)
			{
				DelegateHelper.readPositions(network, deserialiser, node, card);

				@Nullable
				final String ethNumber=deserialiser.readAttribute(node, "ethNumber");

				if (ethNumber!=null)
				{
					final int ethNumber1=Integer.parseInt(ethNumber);

					//old versions stored ethNumber of -1 for uninstalled card.
					if (ethNumber1!=-1)
					{
						card.installDeviceDrivers(network).ethNumber=ethNumber1;
					}
				}

				if (card.withDrivers!=null)
				{
					final String ipAddress=deserialiser.readAttribute(node, "ipAddress");

					try
					{
						card.withDrivers.ipAddress.set(IPAddressUtility.valueOf(ipAddress));
					}
					catch (final CheckedNumberFormatException exception)
					{
						return impossible();
					}

					final String netMask=deserialiser.readAttribute(node, "netMask");

					try
					{
						card.withDrivers.netMask.set(NetMaskUtility.valueOf(netMask));
					}
					catch (final CheckedNumberFormatException exception)
					{
						return impossible();
					}
				}

				return card;
			}

			@Override
            public Card construct()
			{
				return network.cardFactory.run(PointUtility.origin);
			}

			public boolean canHandle(final Object object)
			{
				return object instanceof Card;
			}

			@Override
            public String getIdentifier()
			{
				return "ipsim.persistence.delegates.EthernetCardDelegate";
			}
		};
	}
}