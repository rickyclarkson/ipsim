package ipsim.network.connectivity.card;

import ipsim.ExceptionHandler;
import ipsim.NetworkContext;
import ipsim.gui.components.PacketSourceVisitor2;
import ipsim.network.Network;
import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.Listeners;
import ipsim.network.connectivity.OutgoingPacketListener;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketSourceAndIndex;
import ipsim.network.connectivity.PacketSourceVisitor;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.ethernet.MacAddress;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.IPAddressUtility;
import java.util.List;
import org.jetbrains.annotations.Nullable;

import static ipsim.Caster.asNotNull;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asCable;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asComputer;
import static ipsim.gui.PositionUtility.getParent;
import static ipsim.util.Collections.arrayList;

public final class Card implements PacketSource {
    @Nullable
    public CardDrivers withDrivers = null;

    public final Listeners<IncomingPacketListener> incomingPacketListeners = new Listeners<IncomingPacketListener>();
    public final Listeners<OutgoingPacketListener> outgoingPacketListeners = new Listeners<OutgoingPacketListener>();
    public final List<PacketSourceAndIndex> children = arrayList();

    public CardDrivers installDeviceDrivers(final Network network) {
        @Nullable
        final PacketSource parent = getParent(network, this, 0);

        final Computer computer = asNotNull(asComputer(asNotNull(parent)));
        final int ethNumber = computer.getFirstAvailableEthNumber();
        network.modified = true;

        withDrivers = new CardDrivers(network, IPAddressUtility.zero, NetMaskUtility.zero, ethNumber, this);
        return withDrivers;
    }

    public void uninstallDeviceDrivers() {
        final CardDrivers drivers = this.withDrivers;

        if (drivers == null) {
            ExceptionHandler.impossible();
        } else {
            if (!(0 == drivers.ipAddress.get().rawValue)) {
                NetworkContext.errors("Cannot uninstall device drivers when the IP address is active (not 0.0.0.0)");
                return;
            }

            drivers.ethNumber = -1;
            withDrivers = null;
        }
    }

    public
    @Nullable
    Cable getCable() {
        if (children.isEmpty())
            return null;

        return asCable(children.iterator().next().packetSource);
    }

    public boolean hasCable() {
        return !children().isEmpty();
    }

    public boolean hasDeviceDrivers() {
        return withDrivers != null;
    }

    public MacAddress getMacAddress(final Network network) {
        if (!network.macAddresses.contains(this))
            network.macAddresses.add(this);

        return new MacAddress(network.macAddresses.indexOf(this) + 1);
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PacketSourceAndIndex> children() {
        return children;
    }

    @Override
    public Listeners<IncomingPacketListener> getIncomingPacketListeners() {
        return incomingPacketListeners;
    }

    @Override
    public Listeners<OutgoingPacketListener> getOutgoingPacketListeners() {
        return outgoingPacketListeners;
    }

    @Override
    public void accept(final PacketSourceVisitor2 visitor) {
        visitor.visit(this);
    }

    @Override
    public <R> R accept(final PacketSourceVisitor<R> visitor) {
        return visitor.visit(this);
    }
}