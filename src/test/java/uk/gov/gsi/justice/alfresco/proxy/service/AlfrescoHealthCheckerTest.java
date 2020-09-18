package uk.gov.gsi.justice.alfresco.proxy.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.gsi.justice.alfresco.proxy.model.AlfrescoHealth;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.http.Fault.EMPTY_RESPONSE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.FAULT;
import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.OK;

public class AlfrescoHealthCheckerTest {
    private static final int PORT = 6067;
    private final String alfrescoBaseUrl = "http://localhost:" + PORT;
    private final String alfrescoHealthCheckPath = "/afresco/s/admin-spg/healthcheck";
    private static final WireMockServer WIREMOCK_SERVER = new WireMockServer(options()
            .port(PORT)
            .notifier(new ConsoleNotifier(true)));

    private final AlfrescoHealthChecker sut = new AlfrescoHealthChecker(alfrescoBaseUrl, alfrescoHealthCheckPath);

    @BeforeClass
    public static void setup() {
        WIREMOCK_SERVER.start();
    }

    @After
    public void after() {
        WIREMOCK_SERVER.resetAll();
    }

    @AfterClass
    public static void clean() throws Exception {
        WIREMOCK_SERVER.shutdown();
        SECONDS.sleep(2);
    }

    @Test
    public void testGetAlfrescoHealth() {
        WIREMOCK_SERVER.stubFor(get(urlEqualTo(alfrescoHealthCheckPath))
                .willReturn(aResponse().withStatus(200)));

        final AlfrescoHealth expectedResult = new AlfrescoHealth(OK, 200, null);

        final AlfrescoHealth actualResult = sut.checkDependencyHealth();

        assertThat(actualResult, is(expectedResult));

        WIREMOCK_SERVER.verify(1, getRequestedFor(urlEqualTo(alfrescoHealthCheckPath)));
    }

    @Test
    public void testGetAlfrescoHealthWhenStatusCodeIsNot200() {
        WIREMOCK_SERVER.stubFor(get(urlEqualTo(alfrescoHealthCheckPath))
                .willReturn(aResponse().withStatus(404)));

        final AlfrescoHealth expectedResult = new AlfrescoHealth(FAULT, 404, null);

        final AlfrescoHealth actualResult = sut.checkDependencyHealth();

        assertThat(actualResult, is(expectedResult));

        WIREMOCK_SERVER.verify(1, getRequestedFor(urlEqualTo(alfrescoHealthCheckPath)));
    }

    @Test
    public void testGetAlfrescoHealthWhenAlfrescoIsNotAvailable() {
        WIREMOCK_SERVER.stubFor(get(urlEqualTo(alfrescoHealthCheckPath))
                .willReturn(aResponse().withFault(EMPTY_RESPONSE)));

        final AlfrescoHealth expectedResult = new AlfrescoHealth(FAULT, 0, "java.net.SocketException: SocketException invoking http://localhost:6067/afresco/s/admin-spg/healthcheck: Unexpected end of file from server");

        final AlfrescoHealth actualResult = sut.checkDependencyHealth();

        assertThat(actualResult, is(expectedResult));

        WIREMOCK_SERVER.verify(getRequestedFor(urlEqualTo(alfrescoHealthCheckPath)));
    }
}