package ipsim.network.connectivity;

import fpeas.maybe.Maybe;
import static fpeas.maybe.MaybeUtility.isJust;
import static fpeas.maybe.MaybeUtility.just;
import static fpeas.maybe.MaybeUtility.nothing;
import ipsim.network.connectivity.arp.ArpPacket;
import ipsim.network.connectivity.arp.ArpPacketUtility;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import ipsim.network.connectivity.ip.IPPacket;
import ipsim.network.ip.IPAddressUtility;
import ipsim.util.Printf;
import org.jetbrains.annotations.Nullable;

public final class PacketUtility2
{
	public static boolean isIPPacket(final Packet packet)
	{
		return isJust(asIPPacket(packet));
	}

	public static Maybe<IPPacket> asIPPacket(final Packet packet)
	{
		return packet.accept(new PacketVisitor2<Maybe<IPPacket>>()
		{
			@Override
            public Maybe<IPPacket> visit(final IPPacket packet1)
			{
				return just(packet1);
			}

			@Override
            public Maybe<IPPacket> visit(final ArpPacket packet1)
			{
				return nothing();
			}

			@Override
            public Maybe<IPPacket> visit(final EthernetPacket packet1)
			{
				return nothing();
			}
		});
	}

	public static boolean isEthernetPacket(final Packet packet)
	{
		return asEthernetPacket(packet)!=null;
	}

	public static @Nullable EthernetPacket asEthernetPacket(final Packet packet)
	{
		return packet.accept(new PacketVisitor2<EthernetPacket>()
		{
			@Override
            public EthernetPacket visit(final IPPacket packet1)
			{
				return null;
			}

			@Override
            public EthernetPacket visit(final ArpPacket packet1)
			{
				return null;
			}

			@Override
            public EthernetPacket visit(final EthernetPacket packet1)
			{
				return packet1;
			}
		});
	}

	public static Maybe<ArpPacket> asArpPacket(final Packet packet)
	{
		return packet.accept(new PacketVisitor2<Maybe<ArpPacket>>()
		{
			@Override
            public Maybe<ArpPacket> visit(final IPPacket packet1)
			{
				return nothing();
			}

			@Override
            public Maybe<ArpPacket> visit(final ArpPacket packet1)
			{
				return just(packet1);
			}

			@Override
            public Maybe<ArpPacket> visit(final EthernetPacket packet1)
			{
				return nothing();
			}
		});
	}

	public static boolean isArpPacket(final Packet packet)
	{
		return isJust(asArpPacket(packet));
	}

	public static String asString(final Packet genPacket)
	{
		return genPacket.accept(new PacketVisitor2<String>()
		{
			@Override
            public String visit(final IPPacket packet)
			{
				return Printf.sprintf("IP packet from %s to %s, containing data: %s", IPAddressUtility.toString(packet.sourceIPAddress.getIPAddress().rawValue),IPAddressUtility.toString(packet.destinationIPAddress.getIPAddress().rawValue), IPDataUtility.asString(packet.data));
			}

			@Override
            public String visit(final ArpPacket packet)
			{
				final boolean request=ArpPacketUtility.isRequest(packet);

				return Printf.sprintf("ArpPacket[%s, %s, %s (%d) to %s (%d)]", packet.id.hashCode(),request ? "REQUEST" : "REPLY", IPAddressUtility.toString(packet.sourceIPAddress.rawValue), packet.sourceMacAddress.rawValue,IPAddressUtility.toString(packet.destinationIPAddress.rawValue), packet.destinationMacAddress.rawValue);
			}

			@Override
            public String visit(final EthernetPacket packet)
			{
				return "EthernetPacket["+packet.sourceAddress.rawValue+"->"+packet.destinationAddress.rawValue+", containing "+asString(packet.data)+']';
			}
		});
	}
}