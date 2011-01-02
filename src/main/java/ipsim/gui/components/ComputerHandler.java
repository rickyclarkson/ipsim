package ipsim.gui.components;

import fj.F;
import fj.data.Option;
import ipsim.Global;
import ipsim.NetworkContext;
import ipsim.awt.Point;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.gui.event.CommandUtility;
import ipsim.image.ImageLoader;
import ipsim.network.Network;
import ipsim.network.connectivity.PacketSniffer;
import ipsim.network.connectivity.PacketSnifferUtility;
import ipsim.network.connectivity.PacketSourceAndIndex;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.ComputerUtility;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetBlockUtility;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.IPAddressUtility;
import ipsim.util.Collections;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextArea;
import org.jetbrains.annotations.NotNull;

import static ipsim.Global.getNetworkContext;
import static ipsim.NetworkContext.errors;
import static ipsim.gui.PositionUtility.getPosition;
import static ipsim.gui.PositionUtility.numPositions;
import static ipsim.gui.PositionUtility.removePositions;
import static ipsim.gui.components.AddDefaultRouteDialogUtility.newInstance;
import static ipsim.gui.components.ContextMenuUtility.item;
import static ipsim.gui.components.RoutingTableEntryEditDialog.createRoutingTableEntryEditDialog;
import static ipsim.lang.Comparators.fromFunction;
import static ipsim.network.ethernet.ComputerUtility.getSortedCards;
import static ipsim.swing.CustomJOptionPane.showYesNoCancelDialog;
import static ipsim.util.Collections.sort2;

public final class ComputerHandler {
    public static final ImageIcon icon = ImageLoader.loadImage(ComputerHandler.class.getResource("/images/computer.png"));
    public static final ImageIcon ispIcon = ImageLoader.loadImage(ComputerHandler.class.getResource("/images/isp.png"));

    public static void render(final NetworkContext context, final Computer computer, final Graphics2D graphics) {
        if (numPositions(computer) == 0)
            throw new RuntimeException("This computer has no position data");

        final Point position = getPosition(context.network, computer, 0);

        final Collection<PacketSourceAndIndex> cards = computer.children();

        final BasicStroke basicStroke = new BasicStroke(8);

        for (final PacketSourceAndIndex component2 : cards) {
            final Point position2 = getPosition(context.network, component2.packetSource, 0);

            graphics.setStroke(basicStroke);

            graphics.drawLine((int) position.x, (int) position.y, (int) position2.x, (int) position2.y);
        }

        final Image computerImage = computer.isISP ? ispIcon.getImage() : icon.getImage();

        final int imageWidth = computerImage.getWidth(null) / 2;

        final int imageHeight = computerImage.getHeight(null) / 2;

        graphics.drawImage(computerImage, (int) position.x - imageWidth, (int) position.y - imageHeight, context.networkView);
    }

    public static JPopupMenu createContextMenu(final Computer computer) {
        final JPopupMenu menu = new JPopupMenu();
        menu.add(item("Ping...", 'P', new Runnable() {
            @Override
            public void run() {
                final PingDialog dialog = new PingDialog(computer);
                dialog.jDialog.setVisible(true);
            }
        }));
        menu.add(item("Traceroute...", 'T', new Runnable() {
            @Override
            public void run() {
                TracerouteDialog.newTracerouteDialog(computer).setVisible(true);
            }
        }));

        final F<Card, Boolean> hasDeviceDrivers = new F<Card, Boolean>() {
            @Override
            public Boolean f(final Card card) {
                return card.hasDeviceDrivers();
            }
        };

        if (Collections.any(computer.getCards(), hasDeviceDrivers))
            menu.addSeparator();

        final F<CardDrivers, Integer> getEthNumber = new F<CardDrivers, Integer>() {
            @Override
            @NotNull
            public Integer f(@NotNull final CardDrivers card) {
                return card.ethNumber;
            }
        };

        final List<CardDrivers> cards = Collections.arrayList();

        for (final CardDrivers card : ComputerUtility.cardsWithDrivers(computer))
            cards.add(card);

        final List<CardDrivers> cards2 = sort2(cards, fromFunction(getEthNumber));

        for (final CardDrivers card : cards2) {
            final JMenu cardMenu = new JMenu("Card " + card.ethNumber);
            if (card.ethNumber < 10)
                cardMenu.setMnemonic((char) ('0' + card.ethNumber));

            cardMenu.add(item("Edit IP Address", 'E', editIPAddress(computer, card)));
            cardMenu.add(item("Assign Automatic IP Address", 'A', assignAutomaticIPAddress()));
            cardMenu.add(item("Remove IP Address", 'R', removeIPAddress(getNetworkContext().network, computer, card)));

            menu.add(cardMenu);
        }

        if (Collections.any(computer.getCards(), hasDeviceDrivers))
            menu.addSeparator();

        menu.add(item("List IP Addresses", 'S', new Runnable() {
            @Override
            public void run() {
                final String display = getIPAddresses(computer);

                final Network network = Global.getNetworkContext().network;
                network.log = Collections.add(network.log, CommandUtility.listIps(computer, network));

                if (display.length() == 0)
                    errors("No IP addresses to display");
                else
                    JOptionPane.showMessageDialog(Global.global.get().frame, display, "IP Addresses", JOptionPane.INFORMATION_MESSAGE);
            }
        }));

        menu.add(item("View/Edit Routing Table", 'R', new Runnable() {
            @Override
            public void run() {
                RoutingTableDialogUtility.createRoutingTableDialog(computer).getJDialog().setVisible(true);

                final Network network = getNetworkContext().network;
                network.log = Collections.add(network.log, CommandUtility.listRoutingTable(computer, network));
            }
        }));

        menu.add(item("Add Explicit Route", 'X', new Runnable() {
            @Override
            public void run() {
                final NetBlock zero = NetBlockUtility.getZero();

                final RouteInfo entry = new RouteInfo(zero, IPAddressUtility.zero);

                final Option<Route> route = Option.none();
                final Option<RoutingTableDialog> dialog = Option.none();

                final JDialog dialog2 = createRoutingTableEntryEditDialog(computer, entry, route, dialog).getDialog();
                dialog2.setLocationRelativeTo(Global.global.get().frame);

                dialog2.setVisible(true);
            }
        }));
        menu.add(item("Add Default Route", 'D', new Runnable() {
            @Override
            public void run() {
                newInstance(computer).setVisible(true);
            }
        }));

        final JRadioButtonMenuItem toggleIPForwarding = new JRadioButtonMenuItem("Toggle IP Forwarding");
        toggleIPForwarding.setMnemonic('G');

        toggleIPForwarding.setSelected(computer.ipForwardingEnabled);

        toggleIPForwarding.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                if (toggleIPForwarding.isSelected()) {
                    computer.ipForwardingEnabled = true;
                    final Network network = getNetworkContext().network;
                    network.modified = true;

                    network.log = Collections.add(network.log, CommandUtility.enableIpForwarding(computer, network));
                } else {
                    computer.ipForwardingEnabled = false;
                    final Network network = Global.getNetworkContext().network;
                    network.modified = true;
                    network.log = Collections.add(network.log, CommandUtility.disableIpForwarding(computer, network));
                }
            }
        });

        menu.add(toggleIPForwarding);

        menu.add(item("Packet Sniffer...", 'N', new Runnable() {
            @Override
            public void run() {
                final Network network = Global.getNetworkContext().network;
                network.log = Collections.add(network.log, "Started a packet sniffer on " + PacketSourceUtility.asString(Global.getNetworkContext().network, computer) + '.');

                final PacketSniffer instance = PacketSnifferUtility.instance(computer);

                computer.getIncomingPacketListeners().add(instance);
                computer.getOutgoingPacketListeners().add(instance);
            }
        }));

        menu.add(item("Show ARP Table...", 'A', new Runnable() {
            @Override
            public void run() {
                final JTextArea area = new JTextArea(5, 20);
                String string = computer.arpTable.asString();
                if (string.trim().length() == 0)
                    string = "No ARP records";

                area.setText(string);
                area.setEditable(false);
                area.setOpaque(false);

                JOptionPane.showMessageDialog(Global.global.get().frame, area, "ARP Table for " + PacketSourceUtility.asString(Global.getNetworkContext().network, computer), JOptionPane.INFORMATION_MESSAGE);
            }
        }));
        menu.add(item("Delete", 'L', new Runnable() {
            @Override
            public void run() {
                if (JOptionPane.YES_OPTION == showYesNoCancelDialog(Global.global.get().frame, "Really delete this computer?", "Delete?")) {
                    final NetworkContext networkContext = Global.getNetworkContext();
                    final Network network = networkContext.network;
                    network.log = Collections.add(network.log, CommandUtility.deleteComputer(computer, networkContext.network));

                    removePositions(networkContext.network, computer, networkContext.networkView);

                    networkContext.network.modified = true;

                    networkContext.networkView.repaint();
                }
            }
        }));

        return menu;
    }

    public static Runnable editIPAddress(final Computer computer, final CardDrivers card) {
        return new Runnable() {
            @Override
            public void run() {
                final String cardNumber = String.valueOf(card.ethNumber);

                EditIPAddressDialogFactory.newInstance(computer, Integer.parseInt(cardNumber)).setVisible(true);
            }
        };
    }

    public static Runnable assignAutomaticIPAddress() {
        return new Runnable() {
            @Override
            public void run() {
                errors("No DHCP server found");
            }
        };
    }

    public static Runnable removeIPAddress(final Network network, final Computer computer, final CardDrivers card) {
        return new Runnable() {
            @Override
            public void run() {
                network.log = Collections.add(network.log, CommandUtility.removeIPAddress(computer, card, network));

                card.ipAddress.set(IPAddressUtility.zero);

                card.netMask.set(NetMaskUtility.zero);
                network.modified = true;
            }
        };
    }

    public static String getIPAddresses(final Computer computer) {
        final Iterable<CardDrivers> collection = getSortedCards(computer);

        final StringBuilder display = new StringBuilder(22);

        for (final CardDrivers cardWithDrivers : collection) {
            final IPAddress ipAddress = cardWithDrivers.ipAddress.get();
            final NetMask netMask = cardWithDrivers.netMask.get();

            if (!(0 == ipAddress.rawValue))
                display.append("Card ").append(cardWithDrivers.ethNumber).append(", IP=").append(IPAddressUtility.toString(ipAddress.rawValue)).append(", netmask=").append(NetMask.asString(netMask.rawValue)).append('\n');
        }

        return display.toString();
    }
}