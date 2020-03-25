package uk.gov.gsi.justice.po.alfresco.proxy.dto;

import com.google.gson.annotations.Expose;

import java.time.Instant;
import java.util.Objects;

public class HealthCheckResponse {
    @Expose()
    private final String name;
    @Expose()
    private final ApiStatus status;
    @Expose()
    private final Dependencies dependencies;
    @Expose()
    private final Instant timestamp;

    public HealthCheckResponse(final String name,
                               final ApiStatus status,
                               final Dependencies dependencies,
                               final Instant timestamp) {
        this.name = name;
        this.status = status;
        this.dependencies = dependencies;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public ApiStatus getStatus() {
        return status;
    }

    public Dependencies getDependencies() {
        return dependencies;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final HealthCheckResponse that = (HealthCheckResponse) o;
        return name.equals(that.name) &&
                status.equals(that.status) &&
                dependencies.equals(that.dependencies) &&
                timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, status, dependencies, timestamp);
    }

    @Override
    public String toString() {
        return "HealthCheckResponse{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", dependencies=" + dependencies +
                ", timestamp=" + timestamp +
                '}';
    }
}
