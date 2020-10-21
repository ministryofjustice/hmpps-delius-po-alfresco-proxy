package uk.gov.gsi.justice.alfresco.proxy.model;

import java.util.Objects;

public class AlfrescoHealth {
  private final DependencyStatus status;
  private final int code;
  private final String message;

  public AlfrescoHealth(DependencyStatus status, int code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }

  public DependencyStatus getStatus() {
    return status;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AlfrescoHealth that = (AlfrescoHealth) o;
    return code == that.code && status == that.status && Objects.equals(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, code, message);
  }

  @Override
  public String toString() {
    return "AlfrescoHealth{"
        + "status="
        + status
        + ", code="
        + code
        + ", message='"
        + message
        + '\''
        + '}';
  }
}
