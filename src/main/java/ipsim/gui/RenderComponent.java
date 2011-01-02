package ipsim.gui;

import ipsim.NetworkContext;
import ipsim.gui.components.ComputerHandler;
import ipsim.gui.components.EthernetCableHandler;
import ipsim.gui.components.EthernetCardHandler;
import ipsim.gui.components.HubHandler;
import ipsim.gui.components.PacketSourceVisitor2;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;
import java.awt.Graphics2D;

public class RenderComponent {
    public static void renderComponent(final NetworkContext context, final PacketSource component, final Graphics2D graphics) {
        component.accept(new PacketSourceVisitor2() {
            @Override
            public void visit(final Card card) {
                EthernetCardHandler.render(context, card, graphics);
            }

            @Override
            public void visit(final Computer computer) {
                ComputerHandler.render(context, computer, graphics);
            }

            @Override
            public void visit(final Cable cable) {
                EthernetCableHandler.render(context.network, cable, graphics);
            }

            @Override
            public void visit(final Hub hub) {
                HubHandler.render(context, hub, graphics);
            }
        });
    }
}
