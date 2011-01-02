package ipsim.persistence.delegates;

import fj.F;
import ipsim.network.Network;
import ipsim.network.connectivity.hub.Hub;
import ipsim.network.connectivity.hub.HubFactory;
import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

import static ipsim.Caster.equalT;

public final class HubDelegate {
    public static SerialisationDelegate<Hub> hubDelegate(final Network network) {
        return new SerialisationDelegate<Hub>() {
            @Override
            public void writeXML(final XMLSerialiser serialiser, final Hub hub) {
                serialiser.writeAttribute("isPowerOn", String.valueOf(hub.isPowerOn()));

                DelegateHelper.writePositions(network, serialiser, hub);
            }

            @Override
            public Hub readXML(final XMLDeserialiser deserialiser, final Node node, final Hub hub) {
                @Nullable
                final String power = deserialiser.readAttribute(node, "isPowerOn");

                final F<String, Boolean> equalT = equalT(power);
                final Boolean equal = equalT.f("true");
                hub.setPower(equal);

                DelegateHelper.readPositions(network, deserialiser, node, hub);

                return hub;
            }

            @Override
            public Hub construct() {
                return HubFactory.newHub(network, 0, 0);
            }

            @Override
            public String getIdentifier() {
                return "ipsim.persistence.delegates.HubDelegate";
            }
        };
    }
}