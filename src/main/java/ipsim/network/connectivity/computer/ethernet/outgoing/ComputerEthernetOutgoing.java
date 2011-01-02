package ipsim.network.connectivity.computer.ethernet.outgoing;

import ipsim.Caster;
import ipsim.network.Network;
import ipsim.network.connectivity.OutgoingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketUtility2;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import org.jetbrains.annotations.NotNull;

import static ipsim.Caster.asNotNull;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asComputer;
import static ipsim.network.connectivity.PacketUtility2.asEthernetPacket;

public final class ComputerEthernetOutgoing implements OutgoingPacketListener {
    private final Network network;

    public ComputerEthernetOutgoing(final Network network) {
        this.network = network;
    }

    @Override
    public void packetOutgoing(final Packet packet, final PacketSource source) {
        packetOutgoingImpl(asNotNull(asEthernetPacket(packet)), asNotNull(asComputer(source)));
    }

    private void packetOutgoingImpl(@NotNull final EthernetPacket packet, @NotNull final Computer computer) {
        final boolean[] sane = {true};

        for (final Card card : computer.getCards()) {
            if (Caster.equalT(card.getMacAddress(network)).f(packet.sourceAddress))
                if (sane[0]) {
                    network.packetQueue.enqueueOutgoingPacket(packet, card);

                    sane[0] = false;
                } else
                    throw new RuntimeException();
        }
    }

    @Override
    public String toString() {
        return "ComputerEthernetOutgoing";
    }

    @Override
    public boolean canHandle(final Packet packet, final PacketSource source) {
        return PacketUtility2.isEthernetPacket(packet);
    }
}