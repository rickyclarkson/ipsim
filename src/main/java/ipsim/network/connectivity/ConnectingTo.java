package ipsim.network.connectivity;

import fj.data.Option;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;

public class ConnectingTo {
    public static Option<String> connectingTo(final Network network, final PacketSource thiz, final int thisIndex, final PacketSource that, final int thatIndex) {
        return thiz.accept(new PacketSourceVisitor<Option<String>>() {
            @Override
            public Option<String> visit(final Card card) {
                if (PacketSourceUtility.isComputer(that) && thisIndex == thatIndex && thisIndex == 0)
                    return Option.none();

                if (PacketSourceUtility.isCable(that)) {
                    if (!thiz.children().isEmpty())
                        return Option.some("Cannot connect more than one network cable to a network card");

                    if (thisIndex == 0 && (thatIndex == 0 || thatIndex == 1))
                        return Option.none();
                }

                throw new IllegalStateException("Cannot connect " + PacketSourceUtility.asString(network, thiz) + " to " + PacketSourceUtility.asString(network, that));
            }

            @Override
            public Option<String> visit(final Computer computer) {
                if (!PacketSourceUtility.isCard(that))
                    throw new IllegalStateException("Cannot connect " + PacketSourceUtility.asString(network, thiz) + " to " + PacketSourceUtility.asString(network, that));

                return Option.none();
            }

            @Override
            public Option<String> visit(final Cable cable) {
                return Option.none();
            }

            @Override
            public Option<String> visit(final Hub hub) {
                if (!PacketSourceUtility.isCable(that))
                    return Option.some("Cannot connect a " + PacketSourceUtility.asString(network, that) + " to " + PacketSourceUtility.asString(network, thiz));
                return Option.none();
            }
        });
    }
}