package ipsim.persistence.delegates;

import fj.F;
import ipsim.awt.Point;
import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;

import static ipsim.Caster.asNotNull;

public final class PointDelegate {
    public static final SerialisationDelegate<Point> pointDelegate = new SerialisationDelegate<Point>() {
        @Override
        public void writeXML(final XMLSerialiser serialiser, final Point point) {
            serialiser.writeAttribute("x", String.valueOf(point.x));
            serialiser.writeAttribute("y", String.valueOf(point.y));
        }

        @Override
        public Point readXML(final XMLDeserialiser deserialiser, final Node node, Point point) {
            final F<String, Double> parseDouble = new F<String, Double>() {
                @Override
                @NotNull
                public Double f(@NotNull final String value) {
                    return Double.parseDouble(value);
                }
            };

            point = parseDouble.andThen(point.withX()).f(asNotNull(deserialiser.readAttribute(node, "x")));
            point = parseDouble.andThen(point.withY()).f(asNotNull(deserialiser.readAttribute(node, "y")));

            return point;
        }

        @Override
        public Point construct() {
            return new Point((double) 0, (double) 0);
        }

        @Override
        public String getIdentifier() {
            return "ipsim.persistence.delegates.PointDelegate";
        }
    };
}