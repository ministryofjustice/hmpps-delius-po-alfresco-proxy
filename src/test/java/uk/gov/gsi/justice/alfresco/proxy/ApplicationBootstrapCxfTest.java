package uk.gov.gsi.justice.alfresco.proxy;

//2018/2019
//1,640.25
//
//HMRC self asse: 0300 200 3300
//
//phishing@metrobank.plc.uk
//
//
//
//Dr. Bailey
//
//
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.cloud.contract.wiremock.WireMockSpring;
//import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

//@ExtendWith(SpringExtension.class)
//@SpringBootTest(properties = {"application.name=Alfresco-Proxy", "alfresco.base.url=http://localhost:6067", "alfresco.health.endpoint=/alfresco/service/noms-spg/notificationStatus"},
//        classes = {AppConfig.class, TestConfig.class, ApplicationBootstrap.class},
//        webEnvironment = WebEnvironment.RANDOM_PORT)
class ApplicationBootstrapCxfTest {
//    @LocalServerPort
//    private int port;
//
//    private static final WireMockServer WIREMOCK = new WireMockServer(WireMockSpring.options()
//            .port(6067)
//            .notifier(new ConsoleNotifier(true)));
//
//    private String baseUrl() {
//        return "http://localhost:" + port;
//    }
//
//    @BeforeAll
//    static void setup() {
//        WIREMOCK.start();
//    }
//
//    @AfterAll
//    static void clean() throws Exception {
//        WIREMOCK.shutdown();
//    }
//
////    @Test
//    public void testCxfGetGreeting() {
//        final String requestPath = "/hello";
//
//        final HttpResponse<String> response = Unirest.get(baseUrl() + "/cxf" + requestPath).asString();
//
//        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
//        assertNotNull(response.getBody());
//    }
//
//    @Test
//    public void testCxfGetProxyConfiguration() {
//        final String requestPath = "/details/1234";
//
//        WIREMOCK.stubFor(get(urlEqualTo(requestPath))
//                .willReturn(aResponse()
//                        .withHeader("Content-Type", "application/json")
//                        .withBody("{\"key\":\"value\"}")));
//
//        final HttpResponse<String> response = Unirest.get(baseUrl() + "/cxf" + requestPath).asString();
//
//        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
//        assertNotNull(response.getBody());
//
//        WIREMOCK.verify(1, getRequestedFor(urlEqualTo(requestPath)));
//    }
}