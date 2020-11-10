package uk.gov.gsi.justice.alfresco.proxy.av;

import uk.gov.gsi.justice.alfresco.proxy.exceptions.AntivirusException;

import java.util.Objects;

public class AntivirusResponse {
    private static final String STREAM_PREFIX = "stream: ";
    private static final String RESPONSE_OK = "stream: OK";
    private static final String FOUND_SUFFIX = "FOUND";
    private static final String ERROR_SUFFIX = "ERROR";

    public enum Status {PASSED, FAILED, ERROR}

    private Status status;
    private String result;
    private AntivirusException exception;

    public AntivirusResponse(AntivirusException avException) {
        this.status = Status.ERROR;
        this.exception = avException;
    }

    public AntivirusResponse(String avResult) {
        setStatusAndResult(avResult);
    }

    public AntivirusResponse(Status status, AntivirusException antivirusException) {
        this.status = status;
        this.exception = antivirusException;
    }

    public AntivirusResponse(Status status) {
        this.status = status;
    }

    private void setStatusAndResult(String avResult) {
        if (avResult == null) {
            status = Status.ERROR;
            exception = new AntivirusException("Response from Antivirus was null");
        } else if (RESPONSE_OK.equals(avResult.trim())) {
            result = avResult;
            status = Status.PASSED;
        } else if (avResult.trim().endsWith(FOUND_SUFFIX)) {
            result = avResult.substring(STREAM_PREFIX.length(), avResult.lastIndexOf(FOUND_SUFFIX) - 1);
            exception = new AntivirusException("Blacklisted: " + result + " FOUND");
            status = Status.FAILED;
        } else if (avResult.trim().endsWith(ERROR_SUFFIX)) {
            status = Status.ERROR;
        }
    }

    public Status getStatus() {
        return status;
    }

    public AntivirusException getException() {
        return exception;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AntivirusResponse that = (AntivirusResponse) o;
        return status == that.status &&
                Objects.equals(exception, that.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, exception);
    }

    @Override
    public String toString() {
        return "AntivirusResponse { exception=" + exception + ", result='" + result + '\'' + ", status=" + status + '}';
    }
}
