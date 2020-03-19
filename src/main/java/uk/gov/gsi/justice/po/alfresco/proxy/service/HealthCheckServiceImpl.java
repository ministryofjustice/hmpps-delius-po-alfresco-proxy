package uk.gov.gsi.justice.po.alfresco.proxy.service;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.gsi.justice.po.alfresco.proxy.dto.Dependencies;
import uk.gov.gsi.justice.po.alfresco.proxy.dto.HealthCheckResponse;
import uk.gov.gsi.justice.po.alfresco.proxy.provider.TimestampProvider;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class HealthCheckServiceImpl implements HealthCheckService {
    private final TimestampProvider timestampProvider;
    private final String applicationName;

    @Inject
    public HealthCheckServiceImpl(TimestampProvider timestampProvider,
                                  @Value("${application.name}") String applicationName) {
        this.timestampProvider = timestampProvider;
        this.applicationName = applicationName;
    }

    @Override
    public HealthCheckResponse checkHealth() {
        final Dependencies dependencies = new Dependencies(new JsonObject(), new JsonObject());
        return new HealthCheckResponse(applicationName, "OK", dependencies, timestampProvider.getTimestamp());
    }
}
