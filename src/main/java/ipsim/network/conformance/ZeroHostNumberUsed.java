package ipsim.network.conformance;

import fj.F;
import fj.data.Option;
import ipsim.network.Network;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.ip.IPAddress;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ipsim.network.NetworkUtility.getAllCards;
import static ipsim.network.conformance.TypicalScores.USUAL;

class ZeroHostNumberUsed extends F<Network, CheckResult> {
    @Override
    @NotNull
    public CheckResult f(@NotNull final Network network) {
        return NonsensicalArrangement.customCheck(getAllCards, new F<Card, Option<String>>() {
            @Override
            @NotNull
            public Option<String> f(@NotNull final Card card) {
                @Nullable
                final CardDrivers cardWithDrivers = card.withDrivers;

                if (cardWithDrivers == null)
                    return Option.none();

                final IPAddress address = cardWithDrivers.ipAddress.get();
                if (0 == address.rawValue)
                    return Option.none();

                if (0 == (address.rawValue & ~cardWithDrivers.netMask.get().rawValue))
                    return Option.some("Card with an all-0s host part of its IP address");

                return Option.none();
            }
        }, NonsensicalArrangement.<Card>noErrors(), USUAL).f(network);
    }
}