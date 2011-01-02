package ipsim.gui.components;

import ipsim.gui.event.CommandUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;
import ipsim.util.Collections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ipsim.gui.PositionUtility.getParent;

public class ComponentMoved {
    public static void componentMoved(final Network network, final PacketSource component, final int... points) {
        component.accept(new PacketSourceVisitor2() {
            @Override
            public void visit(@NotNull final Card card) {
                @Nullable
                final PacketSource parent = getParent(network, card, 0);

                EthernetCardHandler.componentMoved(network, card, points[0]);

                if (parent != null)
                    network.log = Collections.add(network.log, CommandUtility.componentConnect(card, parent, network));
            }

            @Override
            public void visit(@NotNull final Computer computer) {
            }

            @Override
            public void visit(@NotNull final Cable cable) {
                EthernetCableHandler.componentMoved(network, cable, points);
            }

            @Override
            public void visit(@NotNull final Hub hub) {
            }
        });
    }
}