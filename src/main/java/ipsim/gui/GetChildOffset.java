package ipsim.gui;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.awt.Point;
import ipsim.awt.PointUtility;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import static ipsim.gui.PositionUtility.getPosition;
import static ipsim.gui.PositionUtility.setParent;
import ipsim.lang.Assertion;
import ipsim.network.Network;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketSourceVisitor;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.ComputerFactory;
import ipsim.network.connectivity.hub.Hub;

import java.util.List;

public class GetChildOffset
{
	public static final UnitTest test=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final Network context=new Network();
			final Computer computer=ComputerFactory.newComputer(context, 100, 100);
			final Card card=context.cardFactory.run(new Point(40, 40));

			setParent(context,card, 0, computer, 0);

			final Point childOffset=getChildOffset(computer, card);
			Assertion.assertFalse(childOffset.x==0 && childOffset.y==0);

			return !getPosition(context,card,0).equals(getPosition(context,computer,0));
		}

		public String toString()
		{
			return "GetChildOffset.test";
		}
	};

	public static Point getChildOffset(final PacketSource parent, final PacketSource child)
	{
		Assertion.assertNotNull(parent);
		return parent.accept(new PacketSourceVisitor<Point>()
		{
			@Override
            public Point visit(final Card card)
			{
				return PointUtility.origin;
			}

			@Override
            public Point visit(final Computer computer)
			{
				final List<Card> cards=computer.getCards();
				final double angle=cards.indexOf(PacketSourceUtility.asCard(child))*2*Math.PI/cards.size();

				final double y=-50*Math.sin(angle);
				return new Point(50*Math.cos(angle), y);
			}

			@Override
            public Point visit(final Cable cable)
			{
				throw new UnsupportedOperationException();
			}

			@Override
            public Point visit(final Hub hub)
			{
				return PointUtility.origin;
			}
		});
	}
}