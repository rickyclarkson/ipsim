package ipsim.gui.components;

import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketSourceVisitor;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;
import javax.swing.JPopupMenu;

import static ipsim.Global.getNetworkContext;

public class CreateContextMenu {
    public static JPopupMenu createContextMenu(final PacketSource component) {
        return component.accept(new PacketSourceVisitor<JPopupMenu>() {
            @Override
            public JPopupMenu visit(final Card card) {
                return EthernetCardHandler.createContextMenu(getNetworkContext(), card);
            }

            @Override
            public JPopupMenu visit(final Computer computer) {
                return ComputerHandler.createContextMenu(computer);
            }

            @Override
            public JPopupMenu visit(final Cable cable) {
                return EthernetCableHandler.createContextMenu(cable);
            }

            @Override
            public JPopupMenu visit(final Hub hub) {
                return HubHandler.createContextMenu(getNetworkContext(), hub);
            }
        });
    }
}