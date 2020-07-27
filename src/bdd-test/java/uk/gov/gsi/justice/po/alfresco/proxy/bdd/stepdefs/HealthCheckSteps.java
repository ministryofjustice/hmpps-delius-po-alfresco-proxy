package uk.gov.gsi.justice.po.alfresco.proxy.bdd.stepdefs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.java8.En;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.http.Fault.EMPTY_RESPONSE;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@DirtiesContext
public class HealthCheckSteps extends AbstractSteps implements En {
    @Value("${alfresco.base.url}")
    private String alfrescoBaseUrl;

    public HealthCheckSteps() {
        Before(() -> {
            when(timestampProvider.getTimestamp()).thenReturn(timestamp);
        });

        Given("^the PO Alfresco Proxy API is running$", () -> {
            final String statusUp = "expectations/actuator_health.json";
            final String jsonFile = jsonReader.readFile(statusUp);
            final JsonObject expectedActuatorHealth = JsonParser.parseString(jsonFile).getAsJsonObject();

            final HttpResponse<String> httpResponse = Unirest.get(baseUrl() + "/actuator/health").asString();

            assertThat(httpResponse.getStatus(), is(HttpStatus.OK.value()));
            assertThat(httpResponse.getBody(), is(gson.toJson(expectedActuatorHealth)));
        });

        And("^alfresco is healthy$", () -> {
            alfrescoNotificationStatus = alfrescoHealthCheckSampleResponse();
            world.getWireMockServer().stubFor(get(urlEqualTo(alfrescoHealthEndpoint))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(gson.toJson(alfrescoNotificationStatus))));
        });

        But("^alfresco is not healthy$", () -> world.getWireMockServer().stubFor(get(urlEqualTo(alfrescoHealthEndpoint)).willReturn(aResponse().withFault(EMPTY_RESPONSE))));

        When("^I request the health of the PO Alfresco Proxy API$", () -> {
            sendRequest(apiHealthEndpoint);
        });

        Then("^(?:a|an) (?:stable|unstable) response per the JSON \"([^\"]*)\" is returned$", (String filename) -> {
            final String jsonFile = jsonReader.readFile(filename)
                    .replace("${time_stamp}", timestamp.toString());
            final JsonObject healthCheckResponse = JsonParser.parseString(jsonFile).getAsJsonObject();
            final String expectedResponse = gson.toJson(healthCheckResponse);

            assertThat(world.getResponseEntity().getStatus(), is(HttpStatus.OK.value()));
            assertTrue(world.getResponseEntity().getHeaders().containsKey("Content-Type"));
            assertThat(world.getResponseEntity().getHeaders().get("Content-Type"), hasItem(contentType.toString()));
            assertThat(world.getResponseEntity().getBody(), is(expectedResponse));

            world.getWireMockServer().verify(getRequestedFor(urlEqualTo(alfrescoHealthEndpoint)));
        });
    }
}