package uk.gov.gsi.justice.po.alfresco.proxy.dto;

import com.google.gson.annotations.Expose;

import java.time.Instant;
import java.util.Objects;

public class HealthCheckResponse {
    @Expose()
    private final String name;
    @Expose()
    private final String status;
    @Expose()
    private final Dependencies dependencies;
    @Expose()
    private final Instant timestamp;

    public HealthCheckResponse(String name, String status, Dependencies dependencies, Instant timestamp) {
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

    public Dependencies getDependencies() {
        return dependencies;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthCheckResponse that = (HealthCheckResponse) o;
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
