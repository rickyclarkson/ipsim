package ipsim.persistence;

import ipsim.lang.Assertion;
import ipsim.util.Collections;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import static ipsim.ExceptionHandler.impossible;

public final class XMLSerialiser {
    private final Writer writer;

    private final List<Object> alreadyStored = Collections.arrayList();

    private void write(final String string) {
        try {
            writer.write(string);
        } catch (IOException e) {
            impossible();
        }
    }

    public XMLSerialiser(final Writer writer) {
        this.writer = writer;

        write("<!DOCTYPE object [\n");
        write("<!ELEMENT object (object|attribute)*>\n");

        write("<!ATTLIST object\n");
        write("name CDATA #IMPLIED\n");
        write("id CDATA #REQUIRED\n");
        write("serialiser CDATA #REQUIRED\n");
        write(">\n");

        write("<!ELEMENT attribute EMPTY>\n");

        write("<!ATTLIST attribute\n");
        write("name CDATA #REQUIRED\n");
        write("value CDATA #REQUIRED\n");
        write(">\n");

        write("]>\n");
    }

    public <T> void writeObject(@NotNull final T object, @NotNull final String name, final SerialisationDelegate<T> serialisable) {
        Assertion.assertNotNull(object, name);

        int id = alreadyStored.indexOf(object);

        final int x = -1;
        if (x == id)
            id = alreadyStored.size();

        write("<object name=\"" + name + "\" serialiser=\"" + serialisable.getIdentifier() + "\" id=\"" + id + "\">");

        if (id == alreadyStored.size()) {
            alreadyStored.add(object);

            serialisable.writeXML(this, object);
        }

        write("</object>");
    }

    public void writeAttribute(final String name, final String value) {
        write("<attribute name=\"" + xmlEncode(name) + "\" value=\"" + xmlEncode(value) + "\"/>");
    }

    private static String xmlEncode(final String value) {
        return value.replaceAll("\"", "&quot;");
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            impossible();
        }
    }
}