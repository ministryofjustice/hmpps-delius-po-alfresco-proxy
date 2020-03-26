package uk.gov.gsi.justice.po.alfresco.proxy.model;

import java.util.Objects;

public class HttpFault {
    private final Integer httpStatusCode;
    private final String errorMessage;

    public HttpFault(final Integer httpStatusCode, final String errorMessage) {
        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final HttpFault httpFault = (HttpFault) o;
        return Objects.equals(httpStatusCode, httpFault.httpStatusCode) &&
                Objects.equals(errorMessage, httpFault.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpStatusCode, errorMessage);
    }

    @Override
    public String toString() {
        return "HttpFault{" +
                "httpStatusCode=" + httpStatusCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
