package ipsim.persistence.delegates;

import fj.F;
import ipsim.Caster;
import ipsim.network.Problem;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.CheckedNumberFormatException;
import ipsim.network.ip.IPAddressUtility;
import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;

import static ipsim.network.ip.IPAddressUtility.valueOf;

public final class ProblemDelegate {
    public static SerialisationDelegate<Problem> problemDelegate = new SerialisationDelegate<Problem>() {
        @Override
        public void writeXML(final XMLSerialiser serialiser, final Problem problem) {
            serialiser.writeAttribute("networkNumber", IPAddressUtility.toString(problem.netBlock.networkNumber.rawValue));
            serialiser.writeAttribute("subnetMask", NetMask.asString(problem.netBlock.netMask.rawValue));
            serialiser.writeAttribute("numberOfSubnets", String.valueOf(problem.numberOfSubnets));
        }

        @Override
        public Problem readXML(final XMLDeserialiser deserialiser, final Node node, final Problem object) {
            final F<String, IPAddress> parse = new F<String, IPAddress>() {
                @Override
                @NotNull
                public IPAddress f(@NotNull final String ipAddress) {
                    try {
                        return valueOf(ipAddress);
                    } catch (CheckedNumberFormatException exception) {
                        throw new RuntimeException(exception);
                    }
                }
            };

            final F<String, NetMask> parseNetMask = new F<String, NetMask>() {
                @Override
                @NotNull
                public NetMask f(@NotNull final String netMask) {
                    try {
                        return NetMaskUtility.valueOf(netMask);
                    } catch (CheckedNumberFormatException exception) {
                        throw new RuntimeException(exception);
                    }
                }
            };

            final IPAddress address = parse.f(Caster.asNotNull(deserialiser.readAttribute(node, "networkNumber")));
            final NetMask mask = parseNetMask.f(Caster.asNotNull(deserialiser.readAttribute(node, "subnetMask")));

            final NetBlock block = new NetBlock(address, mask);

            return new Problem(block, Integer.parseInt(deserialiser.readAttribute(node, "numberOfSubnets")));
        }

        @Override
        public Problem construct() {
            return null;
        }

        @Override
        public String getIdentifier() {
            return "ipsim.persistence.delegates.ProblemDelegate";
        }
    };
}