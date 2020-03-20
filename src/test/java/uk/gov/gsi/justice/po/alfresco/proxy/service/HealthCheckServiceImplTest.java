package uk.gov.gsi.justice.po.alfresco.proxy.service;

import com.google.gson.JsonObject;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.gsi.justice.po.alfresco.proxy.AbstractBaseTest;
import uk.gov.gsi.justice.po.alfresco.proxy.bdd.ioc.TestConfig;
import uk.gov.gsi.justice.po.alfresco.proxy.dto.Dependencies;
import uk.gov.gsi.justice.po.alfresco.proxy.dto.HealthCheckResponse;
import uk.gov.gsi.justice.po.alfresco.proxy.http.RestHttpClient;
import uk.gov.gsi.justice.po.alfresco.proxy.ioc.AppConfig;
import uk.gov.gsi.justice.po.alfresco.proxy.model.HttpSuccess;
import uk.gov.gsi.justice.po.alfresco.proxy.provider.TimestampProvider;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.gsi.justice.po.alfresco.proxy.model.ApiStatus.STABLE;

@ActiveProfiles("HealthCheckServiceImplTest")
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "alfresco.base.url=http://localhost:6067",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = {AppConfig.class, TestConfig.class, HealthCheckServiceImplTest.HealthCheckServiceImplTestConfiguration.class})
class HealthCheckServiceImplTest extends AbstractBaseTest {
    @Profile("HealthCheckServiceImplTest")
    @Configuration
    static class HealthCheckServiceImplTestConfiguration {
        @Bean
        @Primary
        public RestHttpClient provideRestHttpClient() {
            return mock(RestHttpClient.class);
        }
    }

    @Inject
    private RestHttpClient restHttpClient;
    @Inject
    private TimestampProvider timestampProvider;

    private HealthCheckService sut;

    @BeforeEach
    public void prepare() throws Exception {
        sut = new HealthCheckServiceImpl(restHttpClient, timestampProvider, serviceName, alfrescoHealthEndpoint);

        alfrescoNotificationStatus = alfrescoHealthCheckSampleResponse();
        final HttpSuccess httpSuccess = new HttpSuccess(200, stableText, gson.toJson(alfrescoNotificationStatus));
        when(restHttpClient.getResource(alfrescoHealthEndpoint)).thenReturn(Either.right(httpSuccess));
        when(timestampProvider.getTimestamp()).thenReturn(timestamp);
    }

    @Test
    public void testHealthCheck() {
        final Dependencies dependencies = new Dependencies(alfrescoNotificationStatus, new JsonObject());
        final HealthCheckResponse expectedResponse = new HealthCheckResponse(serviceName, STABLE, dependencies, timestamp);

        final HealthCheckResponse actualResponse = sut.checkHealth();

        assertThat(actualResponse, is(expectedResponse));
    }
}