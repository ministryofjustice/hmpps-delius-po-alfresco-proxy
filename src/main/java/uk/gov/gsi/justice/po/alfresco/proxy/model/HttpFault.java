package uk.gov.gsi.justice.po.alfresco.proxy.model;

import java.util.Objects;

public class HttpFault {
    private final Integer code;
    private final String message;
    private final String errorMessage;

    public HttpFault(final Integer code, final String message, final String errorMessage) {
        this.code = code;
        this.message = message;
        this.errorMessage = errorMessage;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpFault httpFault = (HttpFault) o;
        return Objects.equals(code, httpFault.code) &&
                Objects.equals(message, httpFault.message) &&
                Objects.equals(errorMessage, httpFault.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, errorMessage);
    }

    @Override
    public String toString() {
        return "HttpFault{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
