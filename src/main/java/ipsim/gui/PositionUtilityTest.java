package ipsim.gui;

import com.rickyclarkson.testsuite.UnitTest;
import fj.F;
import ipsim.Caster;
import ipsim.awt.Point;
import ipsim.lang.Assertion;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.ComputerFactory;
import ipsim.network.connectivity.hub.Hub;
import ipsim.network.connectivity.hub.HubFactory;

import static ipsim.Caster.asNotNull;
import static ipsim.Caster.equalT;
import static ipsim.gui.PositionUtility.getParent;
import static ipsim.gui.PositionUtility.getPosition;
import static ipsim.gui.PositionUtility.setParent;
import static ipsim.gui.PositionUtility.setPosition;
import static ipsim.lang.Assertion.assertTrue;
import static ipsim.util.Collections.any;
import static ipsim.util.Collections.mapWith;

public final class PositionUtilityTest
{
	public static final UnitTest testRetention=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final Network network=new Network();

			final Card card=network.cardFactory.f(new Point(0, 0));

			final Computer computer=ComputerFactory.newComputer(network, 0, 0);
			computer.computerID=network.generateComputerID();

			setParent(network, card, 0, computer, 0);

			return equalT(getParent(network, card, 0), computer);
		}

		public String toString()
		{
			return "test retention";
		}
	};

	public static final UnitTest testRetention2=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final Network network=new Network();

			final Cable cable=network.cableFactory.newCable(0, 0, 50, 0);

			final Card card1=network.cardFactory.f(new Point(0, 0));

			final Card card2=network.cardFactory.f(new Point(0, 0));

			setPosition(network, card1, mapWith(0, new Point((double)200, (double)200)));
			Assertion.assertTrue(getPosition(network, card1, 0).x==200);
			setPosition(network, card2, mapWith(0, new Point((double)300, (double)300)));

			setParent(network, cable, 0, card1, 0);

			setParent(network, cable, 1, card2, 0);

			final Boolean equal1=equalT(getParent(network, cable, 0),card1);
			return equal1 && equalT(getParent(network, cable, 1),card2);
		}

		public String toString()
		{
			return "test retention 2";
		}
	};

	public static final UnitTest setParentTwice=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final Network network=new Network();
			final Card card=network.cardFactory.f(new Point(5, 5));
			final Hub hub=HubFactory.newHub(network, 10, 10);
			final Cable cable=network.cableFactory.newCable(20, 20, 40, 40);

			setParent(network, cable, 0, card, 0);
			setParent(network, cable, 0, hub, 0);
			return equalT(getParent(network, cable, 0), hub);
		}

		public String toString()
		{
			return "setParentTwice";
		}
	};

	public static final UnitTest cableWithTwoEnds=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final Network network=new Network();
			final Computer computer1=ComputerFactory.newComputer(network, 50, 50);
			final Computer computer2=ComputerFactory.newComputer(network, 100, 100);
			final Card card1=network.cardFactory.f(new Point(25, 25));
			final Card card2=network.cardFactory.f(new Point(75, 75));

			setParent(network, card1, 0, computer1, 0);
			setParent(network, card2, 0, computer2, 0);

			final Cable cable=network.cableFactory.newCable(150, 150, 200, 200);

			setParent(network, cable, 0, card1, 0);
			setParent(network, cable, 1, card2, 0);

			final F<PacketSource, Boolean> equalT=Caster.<PacketSource>equalT(cable);

			assertTrue(any(NetworkUtility.getDepthFirstIterable(network), equalT));
			return equalT(asNotNull(getParent(network, cable, 0)), card1) && equalT(asNotNull(getParent(network, cable, 1)), card2);
		}

		public String toString()
		{
			return "PositionUtilityTest.cableWithTwoEnds";
		}
	};

	public static final UnitTest cableTopLevelAndChild=new UnitTest()
	{
		@Override
        public boolean invoke()
		{
			final Network network=new Network();

			final Cable cable=network.cableFactory.newCable(50, 50, 50+50, 50);
			final Card card=network.cardFactory.f(new Point(200,200));

			setParent(network,cable,0,card,0);

			Assertion.assertTrue(network.topLevelComponents.contains(cable));
			PositionUtility.getPosition(network,cable,1); //this throws an exception if the cable is invalid.
			return true;
		}

		public String toString()
		{
			return "cableTopLevelAndChild";
		}
	};
}