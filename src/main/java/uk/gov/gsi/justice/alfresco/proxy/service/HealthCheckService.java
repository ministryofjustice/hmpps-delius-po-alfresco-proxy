package uk.gov.gsi.justice.alfresco.proxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.gsi.justice.alfresco.proxy.model.AlfrescoHealth;
import uk.gov.gsi.justice.alfresco.proxy.model.ApiHealth;
import uk.gov.gsi.justice.alfresco.proxy.utils.TimestampProvider;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.util.Collections.unmodifiableMap;

@Named("alfrescoHealthChecker")
public class HealthCheckService {
    private final static Logger LOGGER = LoggerFactory.getLogger(HealthCheckService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${alfresco.base.url}")
    private String alfrescoBaseUrl;
    @Value("${alfresco.health.endpoint}")
    private String alfrescoHealthCheckPath;
    @Value("${application.name}")
    private String appName;
    @Inject
    private TimestampProvider timestampProvider;

    public HealthCheckService() {
        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public String checkHealth() throws JsonProcessingException {
        final String stable = "STABLE";
        final String unstable = "UNSTABLE";

        final Map<String, Object> dependencies = new HashMap<>();
        ApiHealth apiHealth = null;
        try {
            final WebClient alfrescoClient = WebClient.create(alfrescoBaseUrl + alfrescoHealthCheckPath);
            final Response response = alfrescoClient.get();
            final int code = response.getStatus();

            dependencies.put("alfresco", new AlfrescoHealth(code, null));
            dependencies.put("clamAV", null);
            final String apiStatus = code == 200 ? stable : unstable;
            apiHealth = new ApiHealth(appName, apiStatus, unmodifiableMap(dependencies), timestampProvider.getTimestamp());
        } catch (Exception e) {
            LOGGER.error("Error checking Alfresco health:::", e);
            dependencies.put("alfresco", new AlfrescoHealth(0, e.getMessage()));
            dependencies.put("clamAV", null);
            apiHealth = new ApiHealth(appName, unstable, unmodifiableMap(dependencies), timestampProvider.getTimestamp());
        }

        return objectMapper.writeValueAsString(apiHealth);
    }
}
