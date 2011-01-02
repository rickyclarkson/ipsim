package ipsim.lang;

import java.io.IOException;

public final class CheckedIllegalStateException extends Exception {
    public CheckedIllegalStateException() {
    }

    public CheckedIllegalStateException(final String message) {
        super(message);
    }

    public CheckedIllegalStateException(final IOException exception) {
        super(exception);
    }
}