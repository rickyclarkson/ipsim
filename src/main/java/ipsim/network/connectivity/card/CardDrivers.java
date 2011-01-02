package ipsim.network.connectivity.card;

import ipsim.network.Network;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.property.Property;
import ipsim.property.PropertyListenerUtility;
import java.util.Collection;
import java.util.HashSet;
import org.jetbrains.annotations.Nullable;

import static ipsim.Caster.asNotNull;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asComputer;
import static ipsim.gui.PositionUtility.getParent;
import static ipsim.network.ethernet.ComputerUtility.isLocallyReachable;
import static ipsim.property.PropertyUtility.newProperty;

public final class CardDrivers {
    public int ethNumber;
    public final Property<IPAddress> ipAddress;
    public final Property<NetMask> netMask;
    public final Card card;

    public CardDrivers(final Network network, final IPAddress ipAddress, final NetMask netMask, final int ethNumber, final Card card) {
        this.ipAddress = newProperty(ipAddress);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                @Nullable
                final PacketSource parent = getParent(network, card, 0);

                final Collection<Route> toDelete = new HashSet<Route>();

                final Computer computer = asNotNull(asComputer(parent));

                for (final Route route : computer.routingTable.routes())
                    if (!isLocallyReachable(computer, route.gateway))
                        toDelete.add(route);

                for (final Route route : toDelete)
                    computer.routingTable.remove(route);

                network.modified = true;
            }
        };

        this.ipAddress.addPropertyListener(PropertyListenerUtility.<IPAddress>fromRunnable(runnable));

        this.netMask = newProperty(netMask);
        this.netMask.addPropertyListener(PropertyListenerUtility.<NetMask>fromRunnable(runnable));

        this.ethNumber = ethNumber;
        this.card = card;
    }
}