package ipsim.network.connectivity;

import fj.Effect;
import ipsim.network.connectivity.arp.ArpPacket;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import ipsim.network.connectivity.ip.IPPacket;
import org.jetbrains.annotations.NotNull;

public class PacketVisitorUtility {
    public static final PacketVisitor blank = new PacketVisitor() {
        @Override
        public void visit(@NotNull final IPPacket packet) {
        }

        @Override
        public void visit(@NotNull final ArpPacket packet) {
        }

        @Override
        public void visit(@NotNull final EthernetPacket packet) {
        }
    };

    public static PacketVisitor visitIPPacket(final PacketVisitor base, final Effect<IPPacket> effect) {
        return new PacketVisitor() {
            @Override
            public void visit(@NotNull final IPPacket packet) {
                effect.e(packet);
            }

            @Override
            public void visit(@NotNull final ArpPacket packet) {
                base.visit(packet);
            }

            @Override
            public void visit(@NotNull final EthernetPacket packet) {
                base.visit(packet);
            }
        };
    }

    public static PacketVisitor visitArpPacket(final PacketVisitor base, final Effect<ArpPacket> effect) {
        return new PacketVisitor() {
            @Override
            public void visit(@NotNull final IPPacket packet) {
                base.visit(packet);
            }

            @Override
            public void visit(@NotNull final ArpPacket packet) {
                effect.e(packet);
            }

            @Override
            public void visit(@NotNull final EthernetPacket packet) {
                base.visit(packet);
            }
        };
    }

    public static PacketVisitor visitEthernetPacket(final PacketVisitor base, final Effect<EthernetPacket> effect) {
        return new PacketVisitor() {
            @Override
            public void visit(@NotNull final IPPacket packet) {
                base.visit(packet);
            }

            @Override
            public void visit(@NotNull final ArpPacket packet) {
                base.visit(packet);
            }

            @Override
            public void visit(@NotNull final EthernetPacket packet) {
                effect.e(packet);
            }
        };
    }
}