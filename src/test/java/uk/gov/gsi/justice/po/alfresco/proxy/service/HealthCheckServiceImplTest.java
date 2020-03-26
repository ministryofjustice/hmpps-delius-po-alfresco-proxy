package uk.gov.gsi.justice.po.alfresco.proxy.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import uk.gov.gsi.justice.po.alfresco.proxy.model.HttpFault;
import uk.gov.gsi.justice.po.alfresco.proxy.model.HttpSuccess;
import uk.gov.gsi.justice.po.alfresco.proxy.provider.TimestampProvider;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.gsi.justice.po.alfresco.proxy.dto.ApiStatus.STABLE;
import static uk.gov.gsi.justice.po.alfresco.proxy.dto.ApiStatus.UNSTABLE;

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
    public void prepare() {
        sut = new HealthCheckServiceImpl(restHttpClient, timestampProvider, serviceName, alfrescoHealthEndpoint, gson);

        when(timestampProvider.getTimestamp()).thenReturn(timestamp);
    }

    @Test
    public void testHealthCheck() throws Exception {
        alfrescoNotificationStatus = alfrescoHealthCheckSampleResponse();
        final HttpSuccess httpSuccess = new HttpSuccess(200, stableText, gson.toJson(alfrescoNotificationStatus));
        when(restHttpClient.getResource(alfrescoHealthEndpoint)).thenReturn(Either.right(httpSuccess));

        final Dependencies dependencies = new Dependencies(alfrescoNotificationStatus, new JsonObject());
        final HealthCheckResponse expectedResponse = new HealthCheckResponse(serviceName, STABLE, dependencies, timestamp);

        final HealthCheckResponse actualResponse = sut.checkHealth();

        assertThat(actualResponse, is(expectedResponse));
    }
 
    @Test
    public void testHealthCheckWhenAlfrescoReturnsAnError() {
        final HttpFault httpFault = new HttpFault(0, "unexpected end of stream some random text");
        when(restHttpClient.getResource(alfrescoHealthEndpoint)).thenReturn(Either.left(httpFault));

        final String json = gson.toJson(httpFault);
        final JsonObject faultJson = JsonParser.parseString(json).getAsJsonObject();
        final Dependencies dependencies = new Dependencies(faultJson, new JsonObject());
        final HealthCheckResponse expectedResponse = new HealthCheckResponse(serviceName, UNSTABLE, dependencies, timestamp);

        final HealthCheckResponse actualResponse = sut.checkHealth();

        assertThat(actualResponse, is(expectedResponse));
    }
}