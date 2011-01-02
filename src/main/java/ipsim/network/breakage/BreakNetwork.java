package ipsim.network.breakage;

import fj.Effect;
import fj.F;
import fj.P;
import fj.P2;
import fj.data.Option;
import ipsim.Global;
import ipsim.NetworkContext;
import ipsim.gui.UserMessages;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.conformance.ConformanceTestsUtility;
import ipsim.network.connectivity.ConnectivityResults;
import ipsim.network.connectivity.ConnectivityTest;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.computer.RoutingTable;
import ipsim.network.connectivity.hub.Hub;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ethernet.ComputerUtility;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.util.Collections;
import java.util.Collection;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.jetbrains.annotations.NotNull;

import static ipsim.Global.getNetworkContext;
import static ipsim.NetworkContext.askUserForNumberOfFaults;
import static ipsim.NetworkContext.confirm;
import static ipsim.gui.PositionUtility.getPosition;
import static ipsim.gui.PositionUtility.setPosition;
import static ipsim.gui.UserPermissions.FREEFORM_WITH_BREAKS;
import static ipsim.network.NetworkUtility.getAllCardsWithDrivers;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.NetworkUtility.getAllHubs;
import static ipsim.network.ip.IPAddressUtility.randomIP;
import static ipsim.util.Collections.arrayList;
import static ipsim.util.Collections.mapWith;

public class BreakNetwork {
    public static void breakNetwork() {
        if (!getNetworkContext().userPermissions.allowBreakingNetwork()) {
            NetworkContext.errors("You are not allowed to break the network during a test");
            return;
        }

        if (getNetworkContext().network.modified) {
            if (!confirm("The network has been modified.  Continue anyway?"))
                return;
        }

        final int numberOfFaultsVar = askUserForNumberOfFaults();

        final ProgressMonitor[] monitor = new ProgressMonitor[]{new ProgressMonitor(Global.global.get().frame, "Breaking network                  ", "Breaking network                    ", 0, 100)};

        monitor[0].setMillisToPopup(0);
        monitor[0].setMillisToDecideToPopup(0);
        monitor[0].setProgress(1);

        new SwingWorker<Void, Object>() {
            @Override
            public Void doInBackground() {
                doIt();
                return null;
            }

            private void doIt() {
                getNetworkContext().networkView.ignorePaints.set(true);

                monitor[0].setNote("Testing connectivity");

                final ConnectivityResults results = ConnectivityTest.testConnectivity(Global.getNetworkContext().network, new Effect<String>() {
                    @Override
                    public void e(final String s) {
                        monitor[0].setNote(s);
                    }
                }, new Effect<Integer>() {
                    @Override
                    public void e(final Integer integer) {
                        monitor[0].setProgress(integer);
                    }
                });

                if (results.getPercentConnected() != 100) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(Global.global.get().frame, "The network must be 100% connected before it can be broken", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    Global.getNetworkContext().networkView.ignorePaints.set(false);
                    monitor[0].close();
                    return;
                }

                final String savedNetwork = NetworkUtility.saveToString(Global.getNetworkContext().network);

                final Network network = Global.getNetworkContext().network;
                network.log = Collections.add(network.log, "Breaking network");

                monitor[0].close();

                monitor[0] = new ProgressMonitor(Global.global.get().frame, "Breaking network                  ", "Breaking network                    ", 0, 100);

                monitor[0].setMillisToPopup(0);
                monitor[0].setMillisToDecideToPopup(0);
                monitor[0].setProgress(1);

                monitor[0].setNote("Trying to break the network");
                monitor[0].setProgress(0);

                int totalFailures = 0;
                for (int a = 0; a < numberOfFaultsVar && totalFailures < 50; a++) {
                    monitor[0].setNote("Breaking network (try " + (totalFailures + 1) + "/50)");
                    final Effect<String> noLog = Effect.doNothing();
                    final Effect<Integer> noProgress = Effect.doNothing();
                    final int connectivityBefore = ConnectivityTest.testConnectivity(Global.getNetworkContext().network, noLog, noProgress).getPercentConnected();

                    oneRandomBreakage(getNetworkContext().network);

                    final int connectivityAfter = ConnectivityTest.testConnectivity(getNetworkContext().network, noLog, noProgress).getPercentConnected();

                    if (connectivityAfter >= connectivityBefore) {
                        a = -1;
                        totalFailures++;

                        NetworkUtility.loadFromString(getNetworkContext().network, savedNetwork);
                    }

                    monitor[0].setProgress(100 * a / numberOfFaultsVar);
                }

                if (totalFailures > 49)
                    JOptionPane.showMessageDialog(Global.global.get().frame, "Failed to break network - try a larger network or less faults", "Error", JOptionPane.ERROR_MESSAGE);

                getNetworkContext().network.log.clear();
                monitor[0].close();
                UserMessages.message("Network broken, with " + numberOfFaultsVar + " faults");

                getNetworkContext().userPermissions = FREEFORM_WITH_BREAKS;

                getNetworkContext().networkView.ignorePaints.set(false);
            }
        }.execute();
    }

    private static void oneRandomBreakage(final Network network) {
        randomOneOf(turnAHubOff(network), turnPacketForwardingOffOnARouter(network), changeCableType(network), disconnectCable(network), swapIPsOnARouter(network), changeIP(network), changeNetMask(network), changeRoute(network), deleteRoute(network));
    }

    private static Runnable deleteRoute(final Network network) {
        return new Runnable() {
            @Override
            public void run() {
                for (Computer computer : randomOneOf(getAllComputers(network))) {
                    final RoutingTable table = computer.routingTable;
                    for (final Route route : table.routes())
                        table.remove(route);
                }
            }
        };
    }

    private static Runnable changeRoute(final Network context) {
        return new Runnable() {
            @Override
            public void run() {
                for (Computer computer : randomOneOf(getAllComputers(context))) {
                    final RoutingTable table = computer.routingTable;

                    for (Route route : randomOneOf(table.routes()))
                        computer.routingTable.replace(route, breakRoute(route));
                }
            }
        };
    }

    private static Route breakRoute(final Route route) {
        if (Math.random() < 0.5)
            return new Route(route.block, randomIP());

        if (Math.random() < 0.5)
            return new Route(new NetBlock(route.block.networkNumber, NetMaskUtility.randomNetMask()), route.gateway);

        return new Route(new NetBlock(randomIP(), route.block.netMask), route.gateway);
    }

    private static Runnable changeNetMask(final Network network) {
        return new Runnable() {
            @Override
            public void run() {
                for (CardDrivers cardDrivers : getAllCardsWithDrivers(network))
                    cardDrivers.netMask.set(NetMaskUtility.randomNetMask());
            }
        };
    }

    private static Runnable changeIP(final Network network) {
        return new Runnable() {
            @Override
            public void run() {
                for (CardDrivers card : randomOneOf(getAllCardsWithDrivers(network)))
                    card.ipAddress.set(randomIP());
            }
        };
    }

    private static Runnable swapIPsOnARouter(final Network network) {
        return new Runnable() {
            @Override
            public void run() {
                for (Computer computer : randomOneOf(getAllComputers(network, ConformanceTestsUtility.isARouter()))) {
                    final List<CardDrivers> cards = ComputerUtility.cardsWithDrivers(computer);

                    final P2<CardDrivers, CardDrivers> pair = randomTwoOf(cards);

                    final IPAddress tmp = pair._1().ipAddress.get();
                    pair._1().ipAddress.set(pair._2().ipAddress.get());
                    pair._2().ipAddress.set(tmp);
                }
            }
        };
    }

    private static <T> P2<T, T> randomTwoOf(final List<T> list) {
        final int first = (int) (Math.random() * (double) list.size());

        int second = (int) (Math.random() * (double) (list.size() - 1));

        if (second >= first)
            second++;

        return P.p(list.get(first), list.get(second));
    }

    private static Runnable disconnectCable(final Network network) {
        return new Runnable() {
            @Override
            public void run() {
                final F<Cable, Boolean> connected = new F<Cable, Boolean>() {
                    @Override
                    @NotNull
                    public Boolean f(@NotNull final Cable cable) {
                        return cable.canTransferPackets(network);
                    }
                };

                for (Cable cable : randomOneOf(NetworkUtility.getAllCables(network, connected))) {
                    final int end = (int) (Math.random() * 2.0);
                    setPosition(network, cable, mapWith(end, getPosition(network, cable, end)));
                }
            }
        };
    }

    private static Runnable changeCableType(final Network network) {
        return new Runnable() {
            @Override
            public void run() {
                for (Cable cable : randomOneOf(NetworkUtility.getAllCables(network)))
                    cable.setCableType(cable.getCableType().another());
            }
        };
    }

    private static Runnable turnPacketForwardingOffOnARouter(final Network network) {
        return new Runnable() {
            @Override
            public void run() {
                for (Computer computer : randomOneOf(getAllComputers(network)))
                    if (ConformanceTestsUtility.isARouter().f(computer))
                        computer.ipForwardingEnabled = false;
            }
        };
    }

    private static Runnable turnAHubOff(final Network network) {
        return new Runnable() {
            @Override
            public void run() {
                for (Hub hub : randomOneOf(getAllHubs(network)))
                    if (hub.isPowerOn())
                        hub.setPower(false);
            }
        };
    }

    private static <T> Option<T> randomOneOf(final List<T> list) {
        if (list.isEmpty())
            return Option.none();

        return Option.some(list.get((int) (Math.random() * list.size())));
    }

    private static <T> Option<T> randomOneOf(final Collection<T> collection) {
        return randomOneOf(arrayList(collection));
    }

    private static void randomOneOf(final Runnable... runnables) {
        runnables[((int) (Math.random() * runnables.length))].run();
    }
}