package uk.gov.gsi.justice.alfresco.proxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.gsi.justice.alfresco.proxy.model.AlfrescoHealth;
import uk.gov.gsi.justice.alfresco.proxy.model.ApiHealth;
import uk.gov.gsi.justice.alfresco.proxy.model.ClamAvHealth;
import uk.gov.gsi.justice.alfresco.proxy.utils.TimestampProvider;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.FAULT;

@Named("alfrescoProxyHealthChecker")
public class AlfrescoProxyHealthChecker {
    private final String applicationName;
    private final ObjectMapper objectMapper;
    private final TimestampProvider timestampProvider;
    private final DependencyHealthChecker<AlfrescoHealth> alfrescoHealthChecker;
    private final DependencyHealthChecker<ClamAvHealth> clamAvHealthChecker;

    @Inject
    public AlfrescoProxyHealthChecker(@Value("${application.name}") final String applicationName,
                                      final ObjectMapper objectMapper,
                                      final TimestampProvider timestampProvider,
                                      @Named("AlfrescoHealthChecker") final DependencyHealthChecker<AlfrescoHealth> alfrescoHealthChecker,
                                      @Named("ClamAvHealthChecker") final DependencyHealthChecker<ClamAvHealth> clamAvHealthChecker) {
        this.applicationName = applicationName;
        this.objectMapper = objectMapper;
        this.timestampProvider = timestampProvider;
        this.alfrescoHealthChecker = alfrescoHealthChecker;
        this.clamAvHealthChecker = clamAvHealthChecker;
    }

    public String checkHealth() throws JsonProcessingException {
        final AlfrescoHealth alfrescoHealth = alfrescoHealthChecker.checkDependencyHealth();
        final ClamAvHealth clamAvHealth = clamAvHealthChecker.checkDependencyHealth();

        final Map<String, Object> dependencies = new HashMap<>();
        dependencies.put("alfresco", alfrescoHealth);
        dependencies.put("clamAV", clamAvHealth);

        final String stability = Arrays.asList(alfrescoHealth.getStatus(), clamAvHealth.getStatus()).contains(FAULT) ?
                "UNSTABLE" :
                "STABLE";

        final ApiHealth apiHealth = new ApiHealth(applicationName, stability, unmodifiableMap(dependencies), timestampProvider.getTimestamp());

        return objectMapper.writeValueAsString(apiHealth);
    }
}
