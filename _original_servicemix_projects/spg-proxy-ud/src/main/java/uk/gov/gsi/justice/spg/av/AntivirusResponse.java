package uk.gov.gsi.justice.spg.av;

import uk.gov.gsi.justice.spg.exceptions.AntivirusException;

public class AntivirusResponse {
    private static final String STREAM_PREFIX = "stream: ";
    private static final String RESPONSE_OK = "stream: OK";
    private static final String FOUND_SUFFIX = "FOUND";
    private static final String ERROR_SUFFIX = "ERROR";

    enum Status {PASSED, FAILED, ERROR}

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
    public String toString() {
        return "AntivirusResponse { exception=" + exception + ", result='" + result + '\'' + ", status=" + status + '}';
    }
}
