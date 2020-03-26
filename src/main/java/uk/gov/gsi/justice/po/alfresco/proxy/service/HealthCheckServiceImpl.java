package uk.gov.gsi.justice.po.alfresco.proxy.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.gsi.justice.po.alfresco.proxy.dto.Dependencies;
import uk.gov.gsi.justice.po.alfresco.proxy.dto.HealthCheckResponse;
import uk.gov.gsi.justice.po.alfresco.proxy.http.RestHttpClient;
import uk.gov.gsi.justice.po.alfresco.proxy.dto.ApiStatus;
import uk.gov.gsi.justice.po.alfresco.proxy.model.HttpFault;
import uk.gov.gsi.justice.po.alfresco.proxy.model.HttpSuccess;
import uk.gov.gsi.justice.po.alfresco.proxy.provider.TimestampProvider;

import javax.inject.Inject;
import javax.inject.Named;

import static uk.gov.gsi.justice.po.alfresco.proxy.dto.ApiStatus.STABLE;
import static uk.gov.gsi.justice.po.alfresco.proxy.dto.ApiStatus.UNSTABLE;

@Named
public class HealthCheckServiceImpl implements HealthCheckService {
    private final RestHttpClient restHttpClient;
    private final TimestampProvider timestampProvider;
    private final String applicationName;
    private final String healthEndpoint;
    private final Gson gson;

    @Inject
    public HealthCheckServiceImpl(final RestHttpClient restHttpClient,
                                  final TimestampProvider timestampProvider,
                                  @Value("${application.name}") final String applicationName,
                                  @Value("${alfresco.health.endpoint}") final String healthEndpoint,
                                  final Gson gson) {
        this.restHttpClient = restHttpClient;
        this.timestampProvider = timestampProvider;
        this.applicationName = applicationName;
        this.healthEndpoint = healthEndpoint;
        this.gson = gson;
    }

    @Override
    public HealthCheckResponse checkHealth() {
        final Either<HttpFault, HttpSuccess> either = restHttpClient.getResource(healthEndpoint);

        return either.fold(l -> buildResult(UNSTABLE, gson.toJson(l)), r -> buildResult(STABLE, r.getBody()));
    }

    private HealthCheckResponse buildResult(final ApiStatus status, final String text) {
        final JsonObject bodyJson = JsonParser.parseString(text).getAsJsonObject();
        final Dependencies dependencies = new Dependencies(bodyJson, new JsonObject());
        return new HealthCheckResponse(applicationName, status, dependencies, timestampProvider.getTimestamp());
    }
}
