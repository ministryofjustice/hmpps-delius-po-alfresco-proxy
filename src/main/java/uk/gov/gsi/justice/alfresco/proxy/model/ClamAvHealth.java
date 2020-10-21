package uk.gov.gsi.justice.alfresco.proxy.model;

import java.util.Objects;

public class ClamAvHealth {
  private final DependencyStatus status;
  private final String message;

  public ClamAvHealth(DependencyStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public DependencyStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClamAvHealth that = (ClamAvHealth) o;
    return status == that.status && message.equals(that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, message);
  }

  @Override
  public String toString() {
    return "ClamAvHealth{" + "status=" + status + ", message='" + message + '\'' + '}';
  }
}
