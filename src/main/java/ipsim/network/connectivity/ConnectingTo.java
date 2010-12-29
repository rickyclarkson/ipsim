package ipsim.network.connectivity;

import fpeas.maybe.Maybe;
import static fpeas.maybe.MaybeUtility.just;
import static fpeas.maybe.MaybeUtility.nothing;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;

public class ConnectingTo
{
	public static Maybe<String> connectingTo(final Network network, final PacketSource thiz, final int thisIndex, final PacketSource that, final int thatIndex)
	{
		return thiz.accept(new PacketSourceVisitor<Maybe<String>>()
		{
			@Override
            public Maybe<String> visit(final Card card)
			{
				if (PacketSourceUtility.isComputer(that) && thisIndex==thatIndex && thisIndex==0)
					return nothing();

				if (PacketSourceUtility.isCable(that))
				{
					if (!thiz.children().isEmpty())
						return just("Cannot connect more than one network cable to a network card");

					if (thisIndex==0 && (thatIndex==0 || thatIndex==1))
						return nothing();
				}

				throw new IllegalStateException("Cannot connect "+PacketSourceUtility.asString(network,thiz)+" to "+PacketSourceUtility.asString(network,that));
			}

			@Override
            public Maybe<String> visit(final Computer computer)
			{
				if (!PacketSourceUtility.isCard(that))
					throw new IllegalStateException("Cannot connect "+PacketSourceUtility.asString(network,thiz)+" to "+PacketSourceUtility.asString(network,that));

				return nothing();
			}

			@Override
            public Maybe<String> visit(final Cable cable)
			{
				return nothing();
			}

			@Override
            public Maybe<String> visit(final Hub hub)
			{
				if (!PacketSourceUtility.isCable(that))
					return just("Cannot connect a "+PacketSourceUtility.asString(network,that)+" to "+PacketSourceUtility.asString(network,thiz));
				return nothing();
			}
		});
	}
}