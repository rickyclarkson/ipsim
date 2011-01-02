package ipsim.gui.components;

import ipsim.NetworkContext;
import ipsim.awt.Point;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.ComputerFactory;
import ipsim.network.connectivity.hub.Hub;
import ipsim.network.connectivity.hub.HubFactory;
import ipsim.util.Collections;
import org.jetbrains.annotations.Nullable;

import static ipsim.Caster.asNotNull;
import static ipsim.gui.PositionUtility.getParent;
import static ipsim.gui.PositionUtility.getPosition;
import static ipsim.gui.PositionUtility.getPositionOrParentAsString;
import static ipsim.gui.PositionUtility.numPositions;
import static ipsim.gui.event.CommandUtility.createComponent;

public final class NetworkComponentUtility {
    public static PacketSource create(final NetworkContext context, final Class<?> clazz, final Point point0, final Point point1) {
        context.network.modified = true;

        PacketSource component = null;

        if (clazz.equals(Computer.class))
            component = ComputerFactory.newComputerWithID(context.network, (int) point0.x, (int) point0.y);
        else if (clazz.equals(Hub.class))
            component = HubFactory.newHub(context.network, (int) point0.x, (int) point0.y);
        else if (clazz.equals(Card.class))
            component = context.network.cardFactory.f(new Point((int) point0.x, (int) point0.y));
        else if (clazz.equals(Cable.class))
            component = context.network.cableFactory.newCable((int) point0.x, (int) point0.y, (int) point1.x, (int) point1.y);

        context.networkView.visibleComponents.add(component);

        context.network.log = Collections.add(context.network.log, createComponent(component, context.network));
        return asNotNull(component);
    }

    public static String pointsToStringWithoutDelimiters(final Network network, final PacketSource component) {
        final int length = numPositions(component);

        if (length == 1) {
            @Nullable
            final PacketSource parent = getParent(network, component, 0);

            if (parent == null)
                return "at " + getPosition(network, component, 0).toString();

            return "connected to " + PacketSourceUtility.asString(network, parent);
        }

        if (length == 0 || length > 2)
            throw new UnsupportedOperationException("Can't deal with a length of " + length + " here.");

        return "from " + getPositionOrParentAsString(network, component, 0) + " to " + getPositionOrParentAsString(network, component, 1);
    }

}