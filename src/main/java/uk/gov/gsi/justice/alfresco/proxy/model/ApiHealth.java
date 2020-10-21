package uk.gov.gsi.justice.alfresco.proxy.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class ApiHealth {
  private final String name;
  private final String status;
  private final Map<String, Object> dependencies;
  private final Instant timestamp;

  public ApiHealth(
      String name, String status, Map<String, Object> dependencies, Instant timestamp) {
    this.name = name;
    this.status = status;
    this.dependencies = dependencies;
    this.timestamp = timestamp;
  }

  public String getName() {
    return name;
  }

  public String getStatus() {
    return status;
  }

  public Map<String, Object> getDependencies() {
    return dependencies;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ApiHealth apiHealth = (ApiHealth) o;
    return name.equals(apiHealth.name)
        && status.equals(apiHealth.status)
        && dependencies.equals(apiHealth.dependencies)
        && timestamp.equals(apiHealth.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, status, dependencies, timestamp);
  }

  @Override
  public String toString() {
    return "ApiHealth{"
        + "name='"
        + name
        + '\''
        + ", status='"
        + status
        + '\''
        + ", dependencies="
        + dependencies
        + ", timestamp="
        + timestamp
        + '}';
  }
}
