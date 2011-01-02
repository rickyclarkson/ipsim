package com.rickyclarkson.java.lang;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class Throwables {
    public static String toString(final Throwable throwable) {
        final StringWriter answer = new StringWriter();

        throwable.printStackTrace(new PrintWriter(answer));

        return answer.toString();
    }
}