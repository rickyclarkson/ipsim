package ipsim.gui;

import ipsim.NetworkContext;
import ipsim.awt.Point;
import ipsim.awt.PointUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.PacketSource;
import ipsim.util.Collections;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;

import static ipsim.gui.PositionUtility.getPosition;
import static ipsim.gui.PositionUtility.numPositions;

public class ObjectRenderer {
    public static void render(final NetworkContext context, final PacketSource component, final Graphics2D graphics) {
        final Map<RenderingHints.Key, Object> map = Collections.hashMap();

        map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.setRenderingHints(map);

        RenderComponent.renderComponent(context, component, graphics);
    }

    public static boolean isNear(final Network network, final PacketSource componentHandler1, final int pointIndex1, final PacketSource componentHandler2, final int pointIndex2) {
        final Point point1 = getPosition(network, componentHandler1, pointIndex1);
        final Point point2 = getPosition(network, componentHandler2, pointIndex2);

        return isNear(point1, point2);
    }

    public static boolean isNear(final Point point1, final Point point2) {
        return Math.abs(point1.x - point2.x) < 45 && Math.abs(point1.y - point2.y) < 45;
    }

    public static Point getCentre(final Network network, final PacketSource component) {
        final int numPositions = numPositions(component);

        Point total = new Point((double) 0, (double) 0);

        for (int a = 0; a < numPositions; a++)
            total = PointUtility.add(total, getPosition(network, component, a));

        return PointUtility.div(total, numPositions);
    }
}