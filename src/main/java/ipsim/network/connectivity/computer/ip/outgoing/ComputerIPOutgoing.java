package ipsim.network.connectivity.computer.ip.outgoing;

import ipsim.Caster;
import ipsim.Globals;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.OutgoingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketQueue;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketUtility2;
import ipsim.network.connectivity.arp.ArpPacket;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.NoSuchRouteException;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import ipsim.network.connectivity.ethernet.MacAddress;
import ipsim.network.connectivity.icmp.ping.PingData;
import ipsim.network.connectivity.icmp.ttl.TimeExceededData;
import ipsim.network.connectivity.icmp.unreachable.UnreachableData;
import ipsim.network.connectivity.ip.DestIPAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.IPDataVisitor;
import ipsim.network.connectivity.ip.IPPacket;
import ipsim.network.connectivity.ip.SourceIPAddress;
import ipsim.network.ethernet.CardUtility;
import ipsim.network.ip.IPAddressUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ipsim.Caster.asNotNull;
import static ipsim.Caster.equalT;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asComputer;
import static ipsim.network.connectivity.PacketUtility2.asIPPacket;
import static ipsim.network.connectivity.computer.RoutingTableUtility.getRouteFor;
import static ipsim.network.connectivity.computer.RoutingTableUtility.hasRouteFor;
import static ipsim.network.ethernet.ComputerUtility.getCardFor;

public final class ComputerIPOutgoing implements OutgoingPacketListener {
    private final Network network;

    public ComputerIPOutgoing(final Network network) {
        this.network = network;
    }

    @Override
    public void packetOutgoing(final Packet packet, final PacketSource source) {
        @NotNull
        final Computer computer = asNotNull(asComputer(source));

        final IPPacket ipPacket = asIPPacket(packet).toNull();

        final DestIPAddress destinationIPAddress = ipPacket.destinationIPAddress;

        final SourceIPAddress sourceIPAddress = ipPacket.sourceIPAddress;

        final boolean hasRouteFor = hasRouteFor(computer, destinationIPAddress);

        if (!hasRouteFor) {
            dropPacket(computer, destinationIPAddress, sourceIPAddress);
            return;
        }

        final Route route;
        try {
            route = getRouteFor(computer, destinationIPAddress);
        } catch (final NoSuchRouteException exception) {
            throw new RuntimeException(exception);
        }

        @Nullable final CardDrivers card = getCardFor(computer, route);

        if (card == null) {
            dropPacket(computer, destinationIPAddress, sourceIPAddress);

            return;
        }

        final IPAddress gatewayIP;

        if (Caster.equalT(route.gateway, card.ipAddress.get()))
            gatewayIP = destinationIPAddress.getIPAddress();
        else
            gatewayIP = route.gateway;

        final PacketQueue queue = network.packetQueue;

        final int rawDestIP = destinationIPAddress.getIPAddress().rawValue;
        if (equalT(card.ipAddress.get(), destinationIPAddress.getIPAddress())) {
            queue.enqueueIncomingPacket(packet, card.card, computer);
            return;
        }

        if (CardUtility.getBroadcastAddress(card).rawValue == rawDestIP) {
            queue.enqueueIncomingPacket(packet, card.card, computer);
            queue.enqueueOutgoingPacket(new EthernetPacket(card.card.getMacAddress(network), new MacAddress(0), ipPacket), card.card);

            return;
        }

        @Nullable
        final MacAddress arpMac = computer.arpTable.getMacAddress(gatewayIP);

        if (arpMac == null) {
            final Object object = new Object();

            final IncomingPacketListener continueListener = new ContinueArpPacketListener(network, ipPacket, object);

            computer.getIncomingPacketListeners().add(continueListener);

            queue.addEmptyQueueListener(new ContinueRemover(continueListener, computer));

            queue.addEmptyQueueListener(new Runnable() {
                @Override
                public void run() {
                    final boolean exceptionHappened = computer.arpTable.getMacAddress(gatewayIP) == null;

                    final Runnable packetDropper = new Runnable() {
                        @Override
                        public void run() {
                            queue.enqueueOutgoingPacket(new IPPacket(new SourceIPAddress(asNotNull(card).ipAddress.get()), new DestIPAddress(ipPacket.sourceIPAddress.getIPAddress()), Globals.DEFAULT_TIME_TO_LIVE, ipPacket.identifier, UnreachableData.HOST_UNREACHABLE), computer);
                        }
                    };

                    if (exceptionHappened)
                        ipPacket.data.accept(new IPDataVisitor() {
                            @Override
                            public void visit(final PingData pingData) {
                                packetDropper.run();
                            }

                            @Override
                            public void visit(final UnreachableData unreachableData) {
                            }

                            @Override
                            public void visit(final TimeExceededData data) {
                            }
                        });
                }
            });

            if (!computer.arpTable.hasEntryFor(gatewayIP)) {
                queue.enqueueOutgoingPacket(new ArpPacket(gatewayIP, new MacAddress(0), card.ipAddress.get(), card.card.getMacAddress(network), object), computer);
            }
        } else {
            queue.enqueueOutgoingPacket(new EthernetPacket(card.card.getMacAddress(network), arpMac, ipPacket), computer);
        }
    }

    void dropPacket(final Computer computer, final DestIPAddress destinationIPAddress, final SourceIPAddress sourceIPAddress) {
        final IPPacket dropPacket = new IPPacket(IPAddressUtility.destToSource(destinationIPAddress), IPAddressUtility.sourceToDest(sourceIPAddress), Globals.DEFAULT_TIME_TO_LIVE, new Object(), UnreachableData.NET_UNREACHABLE);

        network.packetQueue.enqueueIncomingPacket(dropPacket, computer, computer);
    }

    @Override
    public boolean canHandle(final Packet packet, final PacketSource source) {
        return PacketUtility2.isIPPacket(packet) && PacketSourceUtility.isComputer(source);
    }
}