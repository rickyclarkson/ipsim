package ipsim.gui.components;

import anylayout.AnyLayout;
import anylayout.extras.ConstraintUtility;
import anylayout.extras.PercentConstraints;
import anylayout.extras.PercentConstraintsUtility;
import fj.Effect;
import fj.F;
import fj.Function;
import fj.data.Either;
import fj.data.Option;
import ipsim.Global;
import ipsim.awt.ComponentUtility;
import ipsim.gui.event.CommandUtility;
import ipsim.lang.Runnables;
import ipsim.network.Network;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.computer.RoutingTable;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ip.IPAddressUtility;
import ipsim.util.Collections;
import ipsim.util.Node;
import ipsim.util.SequenceUtility;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;

import static ipsim.NetworkContext.errors;
import static ipsim.lang.Stringables.asString;
import static ipsim.network.ethernet.ComputerUtility.getSortedCards;
import static ipsim.network.ethernet.RouteUtility.asCustomString;
import static ipsim.swing.Dialogs.createDialogWithEscapeKeyToClose;
import static ipsim.util.SequenceUtility.cons;
import static ipsim.util.SequenceUtility.empty;
import static ipsim.util.SequenceUtility.reverse;
import static ipsim.util.SequenceUtility.size;

public class RoutingTableDialogUtility {
    public static RoutingTableDialog createRoutingTableDialog(final Computer computer) {
        final JDialog dialog = createDialogWithEscapeKeyToClose(Global.global.get().frame, "Routing Table");

        final JList entries = new JList();

        final JButton editButton = new JButton("Edit...");

        final RoutingTableDialog routingTableDialog = new RoutingTableDialog() {
            public final RoutingTableDialog thiss = this;

            public Option<Node<Either<String, Route>>> list = empty();

            @Override
            public Runnable populateElements() {
                return new Runnable() {
                    @Override
                    public void run() {
                        doStuff();
                    }
                };
            }

            public void doStuff() {
                final RoutingTable routingTable = computer.routingTable;

                final Iterable<Route> allRoutes = routingTable.routes();

                final Iterable<CardDrivers> cards = getSortedCards(computer);

                list = empty();

                for (final CardDrivers cardWithDrivers : cards) {
                    if (!(0 == cardWithDrivers.ipAddress.get().rawValue)) {
                        final IPAddress destination = new IPAddress(cardWithDrivers.netMask.get().rawValue & cardWithDrivers.ipAddress.get().rawValue);

                        final NetMask netmask = cardWithDrivers.netMask.get();

                        final StringBuilder buffer = new StringBuilder();

                        buffer.append("Destination: ");

                        buffer.append(IPAddressUtility.toString(destination.rawValue));

                        buffer.append(" netmask ");

                        buffer.append(NetMask.asString(netmask.rawValue));

                        buffer.append(" Gateway: *");

                        final Either<String, Route> either = Either.left(buffer.toString());
                        list = cons(either, list);
                    }
                }

                for (final Route entry : allRoutes) {
                    final Either<String, Route> either = Either.right(entry);
                    list = cons(either, list);
                }

                list = reverse(list);
                final String[] array = new String[size(list)];

                final F<String, String> identity = Function.identity();

                final int[] a = {0};

                for (Either<String, Route> either : SequenceUtility.iterable(list)) {
                    final F<Route, String> asString = asString();
                    array[a[0]] = either.either(identity, asString);
                    a[0]++;
                }

                entries.setListData(array);

                dialog.invalidate();
                dialog.validate();
                dialog.repaint();
            }

            @Override
            public void editButtonClicked() {
                final int index = entries.getSelectedIndex();

                if (-1 == index) {
                    noEntrySelected();
                    return;
                }

                for (Route entry : SequenceUtility.get(list, entries.getSelectedIndex()).right()) {
                    final RouteInfo entryInfo = new RouteInfo(entry.block, entry.gateway);

                    final JDialog editDialog = RoutingTableEntryEditDialog.createRoutingTableEntryEditDialog(computer, entryInfo, Option.some(entry), Option.some(thiss)).getDialog();

                    editDialog.setVisible(true);
                }
            }

            @Override
            public void deleteButtonClicked() {
                final int index = entries.getSelectedIndex();

                if (-1 == index) {
                    noEntrySelected();
                    return;
                }

                for (Route route : SequenceUtility.get(list, entries.getSelectedIndex()).right()) {
                    final String previous = asCustomString(route);

                    computer.routingTable.remove(route);
                    final Network network = Global.getNetworkContext().network;
                    network.log = Collections.add(network.log, CommandUtility.deleteRoute(computer, previous, Global.getNetworkContext().network));
                }

                populateElements();
            }

            public void noEntrySelected() {
                errors("Select an item before clicking on Edit or Delete");
            }

            @Override
            public JDialog getJDialog() {
                return dialog;
            }

        };

        dialog.setSize(600, 400);

        routingTableDialog.populateElements().run();

        ComponentUtility.centreOnParent(dialog, Global.global.get().frame);

        final PercentConstraints constraints = PercentConstraintsUtility.newInstance(dialog.getContentPane());
        AnyLayout.useAnyLayout(dialog.getContentPane(), 0.5f, 0.5f, constraints.getSizeCalculator(), ConstraintUtility.typicalDefaultConstraint(Runnables.throwRuntimeException));

        constraints.add(new JLabel("Routing Table"), 5, 5, 30, 5, false, false);
        constraints.add(entries, 5, 15, 90, 60, true, true);

        constraints.add(editButton, 10, 85, 20, 10, false, false);

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                routingTableDialog.editButtonClicked();
            }
        });

        final JButton deleteButton = new JButton("Delete");
        constraints.add(deleteButton, 40, 85, 20, 10, false, false);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                routingTableDialog.deleteButtonClicked();
            }
        });

        final JButton closeButton = new JButton("Close");
        constraints.add(closeButton, 70, 85, 20, 10, false, false);

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        return routingTableDialog;
    }

    public static final Effect<RoutingTableDialog> populateElements = new Effect<RoutingTableDialog>() {
        @Override
        public void e(final RoutingTableDialog routingTableDialog) {
            routingTableDialog.populateElements();
        }
    };
}