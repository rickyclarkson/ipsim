package ipsim.persistence.delegates;

import fpeas.function.Function;
import static fpeas.function.FunctionUtility.compose;
import static ipsim.Caster.asNotNull;
import ipsim.awt.Point;
import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;

public final class PointDelegate
{
	public static final SerialisationDelegate<Point> pointDelegate=new SerialisationDelegate<Point>()
	{
		@Override
        public void writeXML(final XMLSerialiser serialiser, final Point point)
		{
			serialiser.writeAttribute("x", String.valueOf(point.x));
			serialiser.writeAttribute("y", String.valueOf(point.y));
		}

		@Override
        public Point readXML(final XMLDeserialiser deserialiser, final Node node, Point point)
		{
			final Function<String, Double> parseDouble=new Function<String, Double>()
			{
				@Override
                @NotNull
				public Double run(@NotNull final String value)
				{
					return Double.parseDouble(value);
				}
			};

			point=compose(parseDouble, point.withX()).run(asNotNull(deserialiser.readAttribute(node, "x")));
			point=compose(parseDouble, point.withY()).run(asNotNull(deserialiser.readAttribute(node, "y")));

			return point;
		}

		@Override
        public Point construct()
		{
			return new Point((double)0, (double)0);
		}

		@Override
        public String getIdentifier()
		{
			return "ipsim.persistence.delegates.PointDelegate";
		}
	};
}