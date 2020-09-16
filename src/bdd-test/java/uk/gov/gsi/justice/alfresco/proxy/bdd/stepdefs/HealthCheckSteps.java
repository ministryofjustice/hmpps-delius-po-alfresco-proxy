package uk.gov.gsi.justice.alfresco.proxy.bdd.stepdefs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.java8.En;
import org.springframework.test.annotation.DirtiesContext;
import uk.gov.gsi.justice.alfresco.proxy.bdd.model.Ping;
import uk.gov.gsi.justice.alfresco.proxy.model.AlfrescoHealth;

import javax.ws.rs.core.Response;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.http.Fault.EMPTY_RESPONSE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@DirtiesContext
public class HealthCheckSteps extends AbstractSteps implements En {

    public HealthCheckSteps() {
        Before(() -> {
            when(timestampProvider.getTimestamp()).thenReturn(timestamp);
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
        });

        But("^alfresco is not healthy$", () -> world.getWireMockServer().stubFor(get(urlEqualTo(alfrescoHealthEndpoint)).willReturn(aResponse().withFault(EMPTY_RESPONSE))));

        When("^I request the health of the Alfresco Proxy API$", () -> sendGetRequest(apiHealthEndpoint));

        Then("^a response stating that the service is \"([^\"]*)\" is returned$", (String status) -> {
            final AlfrescoHealth alfrescoHealth = stableText.equalsIgnoreCase(status) ?
                    new AlfrescoHealth(200, null) :
                    new AlfrescoHealth(0, "java.net.SocketException: SocketException invoking http://localhost:6067/afresco/s/admin-spg/healthcheck: Unexpected end of file from server");
            final String alfrescoHealthJson = gson.toJson(alfrescoHealth);

            assertThat(world.getResponse().getStatus(), is(200));
            assertTrue(world.getResponse().getHeaders().containsKey("Content-Type"));
            assertThat(world.getResponse().getHeaders().get("Content-Type"), hasItem("application/json"));

            final String actualJsonBody = world.getResponse().readEntity(String.class);

            assertThatJson(actualJsonBody).and(
                    x -> x.node("name").isEqualTo(serviceName),
                    x -> x.node("status").isEqualTo(status.toUpperCase()),
                    x -> x.node("dependencies.alfresco").isEqualTo(json(alfrescoHealthJson)),
                    x -> x.node("dependencies.clamAV").isNull(),
                    x -> x.node("timestamp").isEqualTo(timestamp.toString())
            );

            world.getWireMockServer().verify(getRequestedFor(urlEqualTo(alfrescoHealthEndpoint)));
        });
    }
}