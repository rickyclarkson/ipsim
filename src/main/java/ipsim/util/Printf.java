package ipsim.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class Printf {
    private Printf() {
    }

    public static String sprintf(final String format, final Object... args) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);

        writer.printf(format, args);

        return stringWriter.toString();
    }
}