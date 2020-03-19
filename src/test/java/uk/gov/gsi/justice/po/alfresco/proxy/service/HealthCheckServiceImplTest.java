package uk.gov.gsi.justice.po.alfresco.proxy.service;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.gsi.justice.po.alfresco.proxy.AbstractBaseTest;
import uk.gov.gsi.justice.po.alfresco.proxy.bdd.ioc.TestConfig;
import uk.gov.gsi.justice.po.alfresco.proxy.dto.Dependencies;
import uk.gov.gsi.justice.po.alfresco.proxy.dto.HealthCheckResponse;
import uk.gov.gsi.justice.po.alfresco.proxy.ioc.AppConfig;
import uk.gov.gsi.justice.po.alfresco.proxy.provider.TimestampProvider;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "alfresco.base.url=http://localhost:6067", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = {AppConfig.class, TestConfig.class})
class HealthCheckServiceImplTest extends AbstractBaseTest {
    @Inject
    private TimestampProvider timestampProvider;
    @Value("${alfresco.base.url}")
    private String alfrescoBaseUrl;

    private HealthCheckService sut;

    @BeforeEach
    public void prepare() {
        sut = new HealthCheckServiceImpl(timestampProvider, serviceName);

        when(timestampProvider.getTimestamp()).thenReturn(timestamp);
    }

    @Test
    public void testHealthCheck() {
        final Dependencies dependencies = new Dependencies(new JsonObject(), new JsonObject());
        final HealthCheckResponse expectedResponse = new HealthCheckResponse(serviceName, "OK", dependencies, timestamp);

        final HealthCheckResponse actualResponse = sut.checkHealth();

        assertThat(actualResponse, is(expectedResponse));
    }
}