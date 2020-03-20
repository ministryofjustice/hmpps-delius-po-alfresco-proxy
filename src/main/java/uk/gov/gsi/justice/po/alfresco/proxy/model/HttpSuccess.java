package uk.gov.gsi.justice.po.alfresco.proxy.model;

import java.util.Objects;

public class HttpSuccess {
    private final Integer code;
    private final String message;
    private final String body;

    public HttpSuccess(final Integer code, final String message, final String body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpSuccess that = (HttpSuccess) o;
        return code.equals(that.code) &&
                message.equals(that.message) &&
                body.equals(that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, body);
    }

    @Override
    public String toString() {
        return "HttpSuccess{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
