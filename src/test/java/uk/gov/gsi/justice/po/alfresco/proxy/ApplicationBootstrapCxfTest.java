package uk.gov.gsi.justice.po.alfresco.proxy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.gsi.justice.po.alfresco.proxy.bdd.ioc.TestConfig;
import uk.gov.gsi.justice.po.alfresco.proxy.ioc.AppConfig;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"application.name=Alfresco-Proxy", "alfresco.base.url=http://localhost:6067", "alfresco.health.endpoint=/alfresco/service/noms-spg/notificationStatus"},
        classes = {AppConfig.class, TestConfig.class, ApplicationBootstrap.class},
        webEnvironment = WebEnvironment.RANDOM_PORT)
class ApplicationBootstrapCxfTest {
    @LocalServerPort
    private int port;

    private static final WireMockServer WIREMOCK = new WireMockServer(WireMockSpring.options()
            .port(6067)
            .notifier(new ConsoleNotifier(true)));

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @BeforeAll
    static void setup() {
        WIREMOCK.start();
    }

    @AfterAll
    static void clean() throws Exception {
        WIREMOCK.shutdown();
    }

    @Test
    public void testCxfGetProxyConfiguration() {
        final String requestPath = "/details/1234";

        WIREMOCK.stubFor(get(urlEqualTo(requestPath))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"key\":\"value\"}")));

        final HttpResponse<String> response = Unirest.get(baseUrl() + "/cxf" + requestPath).asString();

        System.out.println("------------ About to verify results ---------------");

        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        assertNotNull(response.getBody());

        WIREMOCK.verify(1, getRequestedFor(urlEqualTo(requestPath)));
    }
}