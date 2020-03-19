package uk.gov.gsi.justice.po.alfresco.proxy.dto;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import java.util.Objects;

public class Dependencies {
    @Expose()
    private final JsonObject alfresco;
    @Expose()
    private final JsonObject clamAV;

    public Dependencies(final JsonObject alfresco, final JsonObject clamAV) {
        this.alfresco = alfresco;
        this.clamAV = clamAV;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependencies that = (Dependencies) o;
        return alfresco.equals(that.alfresco) &&
                clamAV.equals(that.clamAV);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alfresco, clamAV);
    }

    @Override
    public String toString() {
        return "Dependencies{" +
                "alfresco=" + alfresco +
                ", clamAV=" + clamAV +
                '}';
    }
}
