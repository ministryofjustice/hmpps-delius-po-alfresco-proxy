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
import static com.github.tomakehurst.wiremock.http.Fault.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
    public void testSuccessfulGetCallIntoAlfresco() {
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

    @Test
    public void testWhenGetCallIntoAlfrescoCanNotFindResource() {
        wiremock.stubFor(get(urlEqualTo(alfrescoHealthEndpoint))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(alfrescoNotificationStatus))));

        final Either<HttpFault, HttpSuccess> either = sut.getResource("/bad/endpoint");

        assertTrue(either.isLeft());
        final HttpFault httpFailure = either.getLeft();
        assertThat(httpFailure.getHttpStatusCode(), is(404));
        assertThat(httpFailure.getErrorMessage(), is("Not Found"));
    }

    @Test
    public void testWhenGetCallIntoAlfrescoReturnsMalformedResponse() {
        wiremock.stubFor(get(urlEqualTo(alfrescoHealthEndpoint))
                .willReturn(aResponse().withFault(MALFORMED_RESPONSE_CHUNK)));

        final Either<HttpFault, HttpSuccess> either = sut.getResource(alfrescoHealthEndpoint);

        assertTrue(either.isLeft());
        final HttpFault httpFailure = either.getLeft();
        assertThat(httpFailure.getHttpStatusCode(), is(0));
        assertThat(httpFailure.getErrorMessage(), startsWith("unexpected end of stream"));
    }

    @Test
    public void testWhenGetCallIntoAlfrescoReturnsConnectionErrors() {
        wiremock.stubFor(get(urlEqualTo(alfrescoHealthEndpoint))
                .willReturn(aResponse().withFault(RANDOM_DATA_THEN_CLOSE)));

        final Either<HttpFault, HttpSuccess> either = sut.getResource(alfrescoHealthEndpoint);

        assertTrue(either.isLeft());
        final HttpFault httpFailure = either.getLeft();
        assertThat(httpFailure.getHttpStatusCode(), is(0));
        assertThat(httpFailure.getErrorMessage(), startsWith("unexpected end of stream"));
    }

    @Test
    public void testWhenGetCallIntoAlfrescoReturnsAnEmptyResponse() {
        wiremock.stubFor(get(urlEqualTo(alfrescoHealthEndpoint))
                .willReturn(aResponse().withFault(EMPTY_RESPONSE)));

        final Either<HttpFault, HttpSuccess> either = sut.getResource(alfrescoHealthEndpoint);

        assertTrue(either.isLeft());
        final HttpFault httpFailure = either.getLeft();
        assertThat(httpFailure.getHttpStatusCode(), is(0));
        assertThat(httpFailure.getErrorMessage(), startsWith("unexpected end of stream"));
    }

    @Test
    public void testWhenGetCallIntoAlfrescoErrorsWithConnectionRest() {
        wiremock.stubFor(get(urlEqualTo(alfrescoHealthEndpoint))
                .willReturn(aResponse().withFault(CONNECTION_RESET_BY_PEER)));

        final Either<HttpFault, HttpSuccess> either = sut.getResource(alfrescoHealthEndpoint);

        assertTrue(either.isLeft());
        final HttpFault httpFailure = either.getLeft();
        assertThat(httpFailure.getHttpStatusCode(), is(0));
        assertThat(httpFailure.getErrorMessage(), is("Connection reset"));
    }

    @Test
    public void testWhenSocketTimesOutOnGetCallIntoAlfresco() {
        final int tenMinutes = 10*60*1000;
        wiremock.stubFor(get(urlEqualTo(alfrescoHealthEndpoint))
                .willReturn(aResponse().withFixedDelay(tenMinutes)));

        final Either<HttpFault, HttpSuccess> either = sut.getResource(alfrescoHealthEndpoint);

        assertTrue(either.isLeft());
        final HttpFault httpFailure = either.getLeft();
        assertThat(httpFailure.getHttpStatusCode(), is(0));
        assertThat(httpFailure.getErrorMessage(), is("timeout"));
    }
}