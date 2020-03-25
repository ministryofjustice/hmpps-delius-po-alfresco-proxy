package uk.gov.gsi.justice.po.alfresco.proxy.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.gsi.justice.po.alfresco.proxy.AbstractBaseTest;
import uk.gov.gsi.justice.po.alfresco.proxy.ApplicationBootstrap;
import uk.gov.gsi.justice.po.alfresco.proxy.bdd.ioc.TestConfig;
import uk.gov.gsi.justice.po.alfresco.proxy.dto.Dependencies;
import uk.gov.gsi.justice.po.alfresco.proxy.dto.HealthCheckResponse;
import uk.gov.gsi.justice.po.alfresco.proxy.ioc.AppConfig;

import javax.inject.Inject;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.gsi.justice.po.alfresco.proxy.dto.ApiStatus.STABLE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"alfresco.base.url=http://localhost:6067", "alfresco.health.endpoint=/alfresco/service/noms-spg/notificationStatus"},
        classes = {AppConfig.class, TestConfig.class, ApplicationBootstrap.class})
@AutoConfigureMockMvc
@DirtiesContext
class AdminControllerTest extends AbstractBaseTest {
    private static final WireMockServer wiremock = new WireMockServer(WireMockSpring.options()
            .port(6067)
            .notifier(new ConsoleNotifier(true)));
    @Inject
    private MockMvc mockMvc;

    @BeforeAll
    static void setup() {
        wiremock.start();
    }

    @BeforeEach
    void prepare() throws Exception {
        when(timestampProvider.getTimestamp()).thenReturn(timestamp);

        alfrescoNotificationStatus = alfrescoHealthCheckSampleResponse();
        wiremock.stubFor(get(urlEqualTo(alfrescoHealthEndpoint))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(alfrescoNotificationStatus))));
    }

    @AfterEach
    void after() {
        wiremock.resetAll();
    }

    @AfterAll
    static void clean() throws Exception {
        wiremock.shutdown();
        SECONDS.sleep(2);
    }

    @Test
    void testCheckApiHealth() throws Exception {
        final Dependencies dependencies = new Dependencies(alfrescoNotificationStatus, new JsonObject());
        final HealthCheckResponse healthCheckResponse = new HealthCheckResponse(serviceName, STABLE, dependencies, timestamp);
        final String expectedResponse = gson.toJson(healthCheckResponse);

        final RequestBuilder requestBuilder = MockMvcRequestBuilders.get(apiHealthEndpoint)
                .accept(APPLICATION_JSON_VALUE);
        final MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        final MockHttpServletResponse response = result.getResponse();

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentType(), is(contentType.toString()));
        assertThat(response.getContentAsString(), is(expectedResponse));

        wiremock.verify(1, getRequestedFor(urlEqualTo(alfrescoHealthEndpoint)));
    }
}