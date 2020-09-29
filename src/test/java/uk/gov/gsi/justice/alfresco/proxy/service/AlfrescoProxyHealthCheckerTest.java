package uk.gov.gsi.justice.alfresco.proxy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import uk.gov.gsi.justice.alfresco.proxy.model.AlfrescoHealth;
import uk.gov.gsi.justice.alfresco.proxy.model.ApiHealth;
import uk.gov.gsi.justice.alfresco.proxy.model.ClamAvHealth;
import uk.gov.gsi.justice.alfresco.proxy.utils.TimestampProvider;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.util.Collections.unmodifiableMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.FAULT;
import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.OK;

public class AlfrescoProxyHealthCheckerTest {
    private final String unstable = "UNSTABLE";
    private final Instant timestamp = Instant.now();

    private final String applicationName = "SPG Alfresco Proxy";
    private final ObjectMapper objectMapper = configureObjectMapper();
    private final TimestampProvider timestampProvider = mock(TimestampProvider.class);
    @SuppressWarnings("unchecked")
    private final DependencyHealthChecker<AlfrescoHealth> alfrescoHealthChecker = mock(DependencyHealthChecker.class);
    @SuppressWarnings("unchecked")
    private final DependencyHealthChecker<ClamAvHealth> clamAvHealthChecker = mock(DependencyHealthChecker.class);

    private final AlfrescoProxyHealthChecker sut = new AlfrescoProxyHealthChecker(
            applicationName,
            objectMapper,
            timestampProvider,
            alfrescoHealthChecker,
            clamAvHealthChecker
    );

    @Test
    public void testWhenBothAlfrescoAndClamAvAreHealthy() throws Exception {
        final String stable = "STABLE";
        final AlfrescoHealth alfrescoHealth = new AlfrescoHealth(OK, 200, "");
        final ClamAvHealth clamAvHealth = new ClamAvHealth(OK, "ClamAV is healthy");

        when(alfrescoHealthChecker.checkDependencyHealth()).thenReturn(alfrescoHealth);
        when(clamAvHealthChecker.checkDependencyHealth()).thenReturn(clamAvHealth);

        final Map<String, Object> dependencies = new HashMap<>();
        dependencies.put("alfresco", alfrescoHealth);
        dependencies.put("clamAV", clamAvHealth);
        final String expectedResult = objectMapper.writeValueAsString(new ApiHealth(applicationName, stable, unmodifiableMap(dependencies), timestamp));

        final String actualResult = sut.checkHealth();

        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void testWhenBothAlfrescoAndClamAvAreUnhealthy() throws Exception {
        final AlfrescoHealth alfrescoHealth = new AlfrescoHealth(FAULT, 404, "");
        final ClamAvHealth clamAvHealth = new ClamAvHealth(FAULT, "ClamAV is unhealthy");

        when(alfrescoHealthChecker.checkDependencyHealth()).thenReturn(alfrescoHealth);
        when(clamAvHealthChecker.checkDependencyHealth()).thenReturn(clamAvHealth);

        final Map<String, Object> dependencies = new HashMap<>();
        dependencies.put("alfresco", alfrescoHealth);
        dependencies.put("clamAV", clamAvHealth);
        final String expectedResult = objectMapper.writeValueAsString(new ApiHealth(applicationName, unstable, unmodifiableMap(dependencies), timestamp));

        final String actualResult = sut.checkHealth();

        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void testWhenAlfrescoIsHealthyButClamAvIsNot() throws Exception {
        final AlfrescoHealth alfrescoHealth = new AlfrescoHealth(OK, 202, "");
        final ClamAvHealth clamAvHealth = new ClamAvHealth(FAULT, "ClamAV is unhealthy");

        when(alfrescoHealthChecker.checkDependencyHealth()).thenReturn(alfrescoHealth);
        when(clamAvHealthChecker.checkDependencyHealth()).thenReturn(clamAvHealth);

        final Map<String, Object> dependencies = new HashMap<>();
        dependencies.put("alfresco", alfrescoHealth);
        dependencies.put("clamAV", clamAvHealth);
        final String expectedResult = objectMapper.writeValueAsString(new ApiHealth(applicationName, unstable, unmodifiableMap(dependencies), timestamp));

        final String actualResult = sut.checkHealth();

        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void testWhenClamAvIsHealthyButAlfrescoIsNot() throws Exception {
        final AlfrescoHealth alfrescoHealth = new AlfrescoHealth(FAULT, 0, "Alfresco is unhealthy");
        final ClamAvHealth clamAvHealth = new ClamAvHealth(OK, "ClamAV is healthy");

        when(alfrescoHealthChecker.checkDependencyHealth()).thenReturn(alfrescoHealth);
        when(clamAvHealthChecker.checkDependencyHealth()).thenReturn(clamAvHealth);

        final Map<String, Object> dependencies = new HashMap<>();
        dependencies.put("alfresco", alfrescoHealth);
        dependencies.put("clamAV", clamAvHealth);
        final String expectedResult = objectMapper.writeValueAsString(new ApiHealth(applicationName, unstable, unmodifiableMap(dependencies), timestamp));

        final String actualResult = sut.checkHealth();

        assertThat(actualResult, is(expectedResult));
    }

    private ObjectMapper configureObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}