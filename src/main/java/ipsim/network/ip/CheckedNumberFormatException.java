package ipsim.network.ip;

public final class CheckedNumberFormatException extends Exception {
    public CheckedNumberFormatException(final String message) {
        super(message);
    }

    public CheckedNumberFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}