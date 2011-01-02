package ipsim.network.connectivity;

import ipsim.Global;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.network.Network;
import ipsim.swing.ContainerUtility;
import ipsim.swing.WindowUtility;
import ipsim.util.Collections;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import static ipsim.swing.Dialogs.createDialogWithEscapeKeyToClose;

public final class PacketSnifferUtility {
    private PacketSnifferUtility() {
    }

    public static PacketSniffer instance(final PacketSource toBeSniffed) {
        final JDialog dialog = createDialogWithEscapeKeyToClose(Global.global.get().frame, "Packet Sniffer on " + PacketSourceUtility.asString(Global.getNetworkContext().network, toBeSniffed));

        final JTextArea output = new JTextArea(5, 80);
        ContainerUtility.add(dialog, new JScrollPane(output));
        WindowUtility.pack(dialog);
        dialog.setVisible(true);

        final PacketSniffer sniffer = new PacketSniffer() {
            @Override
            public void packetIncoming(final Packet packet, final PacketSource source, final PacketSource destination) {
                final String s = "Incoming " + PacketUtility2.asString(packet) + " to " + PacketSourceUtility.asString(Global.getNetworkContext().network, destination);
                append(s);
            }

            private void append(final String string) {
                output.setText(output.getText() + '\n' + string);
            }

            @Override
            public boolean canHandle(final Packet packet, final PacketSource source) {
                return true;
            }

            @Override
            public void packetOutgoing(final Packet packet, final PacketSource source) {
                final String s = "Outgoing " + PacketUtility2.asString(packet) + " from " + PacketSourceUtility.asString(Global.getNetworkContext().network, source);
                append(s);
            }
        };

        WindowUtility.addWindowListener(dialog, new WindowListener() {
            @Override
            public void windowActivated(final WindowEvent e) {
            }

            @Override
            public void windowClosing(final WindowEvent e) {
                final Network network = Global.getNetworkContext().network;
                network.log = Collections.add(network.log, "Removed a packet sniffer from " + PacketSourceUtility.asString(Global.getNetworkContext().network, toBeSniffed) + '.');

                toBeSniffed.getIncomingPacketListeners().remove(sniffer);
                toBeSniffed.getOutgoingPacketListeners().remove(sniffer);
            }

            @Override
            public void windowClosed(final WindowEvent e) {
            }

            @Override
            public void windowDeactivated(final WindowEvent e) {
            }

            @Override
            public void windowDeiconified(final WindowEvent e) {
            }

            @Override
            public void windowIconified(final WindowEvent e) {
            }

            @Override
            public void windowOpened(final WindowEvent e) {
            }
        });

        return sniffer;
    }
}