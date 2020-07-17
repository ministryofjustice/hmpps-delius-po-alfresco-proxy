package uk.gov.gsi.justice.spg.exceptions;

public class AntivirusException extends RuntimeException {

    public AntivirusException(String message) {
        super(message);
    }

    public AntivirusException(String message, Throwable cause) {
        super(message, cause);
    }
}
