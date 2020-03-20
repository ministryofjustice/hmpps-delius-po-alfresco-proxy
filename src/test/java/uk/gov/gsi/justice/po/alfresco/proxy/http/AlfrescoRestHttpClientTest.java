package uk.gov.gsi.justice.po.alfresco.proxy.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import io.vavr.control.Either;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.gsi.justice.po.alfresco.proxy.AbstractBaseTest;
import uk.gov.gsi.justice.po.alfresco.proxy.bdd.ioc.TestConfig;
import uk.gov.gsi.justice.po.alfresco.proxy.ioc.AppConfig;
import uk.gov.gsi.justice.po.alfresco.proxy.model.HttpFault;
import uk.gov.gsi.justice.po.alfresco.proxy.model.HttpSuccess;

import javax.inject.Inject;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "alfresco.base.url=http://localhost:6067", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = {AppConfig.class, TestConfig.class})
@DirtiesContext
class AlfrescoRestHttpClientTest extends AbstractBaseTest {
    private static WireMockServer wiremock = new WireMockServer(WireMockSpring.options()
            .port(6067)
            .notifier(new ConsoleNotifier(true)));
    @Inject
    private RestClient restClient;
    @Value("${alfresco.base.url}")
    private String alfrescoBaseUrl;

    private RestHttpClient sut;

    @BeforeAll
    static void setup() {
        wiremock.start();
    }

    @BeforeEach
    public void prepare() throws Exception {
        sut = new AlfrescoRestHttpClient(restClient, alfrescoBaseUrl);
        alfrescoNotificationStatus = alfrescoHealthCheckSampleResponse();
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
    public void testInjectionOfImplementationInstance() {
        assertNotNull(sut);
        assertThat(sut, instanceOf(AlfrescoRestHttpClient.class));
    }

    @Test
    public void testSuccessfulCallToAlfrescoHealthEndpoint() {
        wiremock.stubFor(get(urlEqualTo(alfrescoHealthEndpoint))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(alfrescoNotificationStatus))));

        final Either<HttpFault, HttpSuccess> either = sut.getResource(alfrescoHealthEndpoint);

        assertTrue(either.isRight());
        final HttpSuccess httpSuccess = either.get();
        assertThat(httpSuccess.getCode(), is(200));
        assertThat(httpSuccess.getBody(), is(gson.toJson(alfrescoNotificationStatus)));

        wiremock.verify(1, getRequestedFor(urlEqualTo(alfrescoHealthEndpoint)));
    }
}