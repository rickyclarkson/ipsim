package ipsim.persistence.delegates;

import ipsim.network.Network;
import ipsim.network.connectivity.cable.Cable;
import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import org.w3c.dom.Node;

import static ipsim.Caster.equalT;
import static ipsim.ExceptionHandler.impossible;
import static ipsim.network.connectivity.cable.CableType.BROKEN;
import static ipsim.network.connectivity.cable.CableType.CROSSOVER;
import static ipsim.network.connectivity.cable.CableType.STRAIGHT_THROUGH;

public final class EthernetCableDelegate {
    public static SerialisationDelegate<Cable> cableDelegate(final Network network) {
        return new SerialisationDelegate<Cable>() {
            @Override
            public void writeXML(final XMLSerialiser serialiser, final Cable cable) {
                DelegateHelper.writePositions(network, serialiser, cable);
                serialiser.writeAttribute("cableType", cable.getCableType().asString());
            }

            @Override
            public Cable readXML(final XMLDeserialiser deserialiser, final Node node, final Cable cable) {
                DelegateHelper.readPositions(network, deserialiser, node, cable);

                String cableType = deserialiser.readAttribute(node, "cableType");

                if (cableType == null)
                    cableType = STRAIGHT_THROUGH.asString();

                if (equalT(cableType, CROSSOVER.asString()))
                    cable.setCableType(CROSSOVER);
                else if (equalT(cableType, STRAIGHT_THROUGH.asString()))
                    cable.setCableType(STRAIGHT_THROUGH);
                else if (equalT(cableType, BROKEN.asString()))
                    cable.setCableType(BROKEN);
                else
                    return impossible();

                return cable;
            }

            @Override
            public Cable construct() {
                return network.cableFactory.newCable(0, 0, 0 + 50, 0);
            }

            @Override
            public String getIdentifier() {
                return "ipsim.persistence.delegates.EthernetCableDelegate";
            }
        };
    }
}