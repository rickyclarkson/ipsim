package ipsim.persistence;

import org.w3c.dom.Node;

public interface SerialisationDelegate<T> {
    void writeXML(XMLSerialiser serialiser, T object);

    T readXML(XMLDeserialiser deserialiser, Node node, T object);

    T construct();

    String getIdentifier();
}