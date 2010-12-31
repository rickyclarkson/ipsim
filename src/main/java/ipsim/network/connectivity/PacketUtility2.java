package ipsim.network.connectivity;

import fj.data.Option;
import ipsim.network.connectivity.arp.ArpPacket;
import ipsim.network.connectivity.arp.ArpPacketUtility;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import ipsim.network.connectivity.ip.IPPacket;
import ipsim.network.ip.IPAddressUtility;
import ipsim.util.Printf;
import org.jetbrains.annotations.Nullable;

import static fj.data.Option.none;
import static fj.data.Option.some;

public final class PacketUtility2
{
	public static boolean isIPPacket(final Packet packet)
	{
		return asIPPacket(packet).isSome();
	}

	public static Option<IPPacket> asIPPacket(final Packet packet)
	{
		return packet.accept(new PacketVisitor2<Option<IPPacket>>()
		{
			@Override
            public Option<IPPacket> visit(final IPPacket packet1)
			{
				return some(packet1);
			}

			@Override
            public Option<IPPacket> visit(final ArpPacket packet1)
			{
				return none();
			}

			@Override
            public Option<IPPacket> visit(final EthernetPacket packet1)
			{
				return none();
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

	public static Option<ArpPacket> asArpPacket(final Packet packet)
	{
		return packet.accept(new PacketVisitor2<Option<ArpPacket>>()
		{
			@Override
            public Option<ArpPacket> visit(final IPPacket packet1)
			{
				return Option.none();
			}

			@Override
            public Option<ArpPacket> visit(final ArpPacket packet1)
			{
				return Option.some(packet1);
			}

			@Override
            public Option<ArpPacket> visit(final EthernetPacket packet1)
			{
				return Option.none();
			}
		});
	}

	public static boolean isArpPacket(final Packet packet)
	{
		return asArpPacket(packet).isSome();
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