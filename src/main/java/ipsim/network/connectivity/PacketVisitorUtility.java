package ipsim.network.connectivity;

import ipsim.network.connectivity.ip.IPPacket;
import ipsim.network.connectivity.arp.ArpPacket;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import org.jetbrains.annotations.NotNull;
import fpeas.sideeffect.SideEffect;

public class PacketVisitorUtility
{
	public static final PacketVisitor blank=new PacketVisitor()
	{
		@Override
        public void visit(@NotNull final IPPacket packet)
		{
		}

		@Override
        public void visit(@NotNull final ArpPacket packet)
		{
		}

		@Override
        public void visit(@NotNull final EthernetPacket packet)
		{
		}
	};

	public static PacketVisitor visitIPPacket(final PacketVisitor base,final SideEffect<IPPacket> effect)
	{
		return new PacketVisitor()
		{
			@Override
            public void visit(@NotNull final IPPacket packet)
			{
				effect.run(packet);
			}

			@Override
            public void visit(@NotNull final ArpPacket packet)
			{
				base.visit(packet);
			}

			@Override
            public void visit(@NotNull final EthernetPacket packet)
			{
				base.visit(packet);
			}
		};
	}

	public static PacketVisitor visitArpPacket(final PacketVisitor base, final SideEffect<ArpPacket> effect)
	{
		return new PacketVisitor()
		{
			@Override
            public void visit(@NotNull final IPPacket packet)
			{
				base.visit(packet);
			}

			@Override
            public void visit(@NotNull final ArpPacket packet)
			{
				effect.run(packet);
			}

			@Override
            public void visit(@NotNull final EthernetPacket packet)
			{
				base.visit(packet);
			}
		};
	}

	public static PacketVisitor visitEthernetPacket(final PacketVisitor base,final SideEffect<EthernetPacket> effect)
	{
		return new PacketVisitor()
		{
			@Override
            public void visit(@NotNull final IPPacket packet)
			{
				base.visit(packet);
			}

			@Override
            public void visit(@NotNull final ArpPacket packet)
			{
				base.visit(packet);
			}

			@Override
            public void visit(@NotNull final EthernetPacket packet)
			{
				effect.run(packet);
			}
		};
	}
}