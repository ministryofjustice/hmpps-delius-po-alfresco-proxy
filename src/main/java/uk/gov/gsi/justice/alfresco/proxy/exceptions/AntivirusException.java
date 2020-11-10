package uk.gov.gsi.justice.alfresco.proxy.exceptions;

import java.util.Objects;

public class AntivirusException extends RuntimeException {
    private final String message;

    public AntivirusException(String message) {
        super(message);
        this.message = message;
    }

    public AntivirusException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AntivirusException that = (AntivirusException) o;
        return message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}
