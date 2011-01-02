package ipsim.persistence.delegates;

import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import org.w3c.dom.Node;

public final class DefaultCommandDelegate {
    public static final SerialisationDelegate<StringBuffer> defaultCommandDelegate = new SerialisationDelegate<StringBuffer>() {
        @Override
        public void writeXML(final XMLSerialiser serialiser, final StringBuffer object) {
            serialiser.writeAttribute("value", object.toString());
        }

        @Override
        public StringBuffer readXML(final XMLDeserialiser deserialiser, final Node node, final StringBuffer buffer) {
            return buffer.append(deserialiser.readAttribute(node, "value"));
        }

        @Override
        public StringBuffer construct() {
            return new StringBuffer();
        }

        @Override
        public String getIdentifier() {
            return "ipsim.persistence.delegates.DefaultCommandDelegate";
        }
    };
}