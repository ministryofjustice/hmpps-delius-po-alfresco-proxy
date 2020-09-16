package uk.gov.gsi.justice.alfresco.proxy.model;

import java.util.Objects;

public class AlfrescoHealth {
    private final int statusCode;
    private final String message;

    public AlfrescoHealth(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlfrescoHealth that = (AlfrescoHealth) o;
        return statusCode == that.statusCode &&
                message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, message);
    }

    @Override
    public String toString() {
        return "AlfrescoHealth{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}