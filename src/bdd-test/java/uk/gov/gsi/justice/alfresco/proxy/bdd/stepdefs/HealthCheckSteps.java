package uk.gov.gsi.justice.alfresco.proxy.bdd.stepdefs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.java8.En;
import org.springframework.test.annotation.DirtiesContext;
import uk.gov.gsi.justice.alfresco.proxy.bdd.model.Ping;
import uk.gov.gsi.justice.alfresco.proxy.model.AlfrescoHealth;
import uk.gov.gsi.justice.alfresco.proxy.model.ClamAvHealth;

import javax.ws.rs.core.Response;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.http.Fault.EMPTY_RESPONSE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.FAULT;
import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.OK;

@DirtiesContext
public class HealthCheckSteps extends AbstractSteps implements En {
    private AlfrescoHealth alfrescoHealth;
    private ClamAvHealth clamAvHealth;

    public HealthCheckSteps() {
        Before(() -> {
            clamAV.stop();
            SECONDS.sleep(5);

            when(timestampProvider.getTimestamp()).thenReturn(timestamp);
        });

        After(() -> {
            if (!clamAV.isRunning()) {
                clamAV.start();
            }

            when(clamAvConnectionParametersProvider.host()).thenReturn(clamAV.getContainerIpAddress());
            when(clamAvConnectionParametersProvider.port()).thenReturn(clamAV.getFirstMappedPort());
            when(clamAvConnectionParametersProvider.timeout()).thenReturn(clamAVTimeout);
        });

        Given("^the Alfresco Proxy API is running$", () -> {
            final String statusUp = "expectations/ping_response.json";
            final String jsonFile = jsonReader.readFile(statusUp);
            final JsonObject expectedPingResponse = JsonParser.parseString(jsonFile).getAsJsonObject();

            final Response response = webTarget.path("/")
                    .request(APPLICATION_JSON_TYPE)
                    .headers(headers)
                    .get();

            assertThat(response.getStatus(), is(200));
            assertThat(response.readEntity(Ping.class), is(gson.fromJson(expectedPingResponse, Ping.class)));
        });

        And("^alfresco is healthy$", () -> {
            world.getWireMockServer().stubFor(get(urlEqualTo(alfrescoHealthEndpoint))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")));

            alfrescoHealth = new AlfrescoHealth(OK, 200, null);
        });

        And("^clamAV is healthy$", () -> {
            clamAV.start();

            when(clamAvConnectionParametersProvider.host()).thenReturn(clamAV.getContainerIpAddress());
            when(clamAvConnectionParametersProvider.port()).thenReturn(clamAV.getFirstMappedPort());
            when(clamAvConnectionParametersProvider.timeout()).thenReturn(clamAVTimeout);

            clamAvHealth = new ClamAvHealth(OK, "ClamAV 0.102.1/25722/Thu Feb 13 11:45:05 2020");
        });

        But("^alfresco is not healthy$", () -> {
            world.getWireMockServer().stubFor(get(urlEqualTo(alfrescoHealthEndpoint))
                    .willReturn(aResponse().withFault(EMPTY_RESPONSE)));

            alfrescoHealth = new AlfrescoHealth(FAULT, 0, "java.net.SocketException: SocketException invoking http://localhost:6067/afresco/s/admin-spg/healthcheck: Unexpected end of file from server");
        });

        And("^clamAV is not healthy$", () -> {
            clamAV.stop();
            SECONDS.sleep(5);

            when(clamAvConnectionParametersProvider.host()).thenReturn("100.90.80.70");
            when(clamAvConnectionParametersProvider.port()).thenReturn(1234);
            when(clamAvConnectionParametersProvider.timeout()).thenReturn(clamAVTimeout);

            clamAvHealth = new ClamAvHealth(FAULT, "xyz.capybara.clamav.CommunicationException: Error while communicating with the server");
        });

        When("^I request the health of the Alfresco Proxy API$", () -> sendGetRequest(apiHealthEndpoint));

        Then("^a response stating that the service is \"([^\"]*)\" is returned$", (String status) -> {
            final String alfrescoHealthJson = gson.toJson(alfrescoHealth);
            final String clamAvHealthJson = gson.toJson(clamAvHealth);

            assertThat(world.getResponse().getStatus(), is(200));
            assertTrue(world.getResponse().getHeaders().containsKey("Content-Type"));
            assertThat(world.getResponse().getHeaders().get("Content-Type"), hasItem("application/json"));

            final String actualJsonBody = world.getResponse().readEntity(String.class);

            assertThatJson(actualJsonBody).and(
                    x -> x.node("name").isEqualTo(serviceName),
                    x -> x.node("status").isEqualTo(status.toUpperCase()),
                    x -> x.node("dependencies.alfresco").isEqualTo(json(alfrescoHealthJson)),
                    x -> x.node("dependencies.clamAV").isEqualTo(json(clamAvHealthJson)),
//                    x -> x.node("dependencies.clamAV").isObject().hasEntrySatisfying("message", new Condition<Object>("Start of message") {
//                        @Override
//                        public boolean matches(Object field) {
//                            return field.toString().startsWith(clamAvHealthJson);
//                        }
//                    }),
                    x -> x.node("timestamp").isEqualTo(timestamp.toString())
            );

            world.getWireMockServer().verify(getRequestedFor(urlEqualTo(alfrescoHealthEndpoint)));
        });
    }
}