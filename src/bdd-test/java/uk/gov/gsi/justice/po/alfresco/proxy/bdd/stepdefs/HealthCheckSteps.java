package uk.gov.gsi.justice.po.alfresco.proxy.bdd.stepdefs;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.java8.En;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.http.Fault.EMPTY_RESPONSE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@DirtiesContext
public class HealthCheckSteps extends AbstractSteps implements En {
    private static final WireMockServer wiremock = new WireMockServer(WireMockSpring.options()
            .port(6067)
            .notifier(new ConsoleNotifier(true)));

    private HttpResponse<String> healthCheckResponseEntity;

    @Value("${alfresco.base.url}")
    private String alfrescoBaseUrl;

    public HealthCheckSteps() {
        Before(() -> {
            wiremock.start();
            SECONDS.sleep(2);

            when(timestampProvider.getTimestamp()).thenReturn(timestamp);
        });

        After(() -> {
            wiremock.shutdown();
            SECONDS.sleep(2);
        });

        Given("^the PO Alfresco Proxy API is running$", () -> {
            final String statusUp = "expectations/actuator_health.json";
            final String jsonFile = jsonReader.readFile(statusUp);
            final JsonObject expectedActuatorHealth = JsonParser.parseString(jsonFile).getAsJsonObject();

            final HttpResponse<String> httpResponse = Unirest.get(statusEndpoint()).asString();

            assertThat(httpResponse.getStatus(), is(HttpStatus.OK.value()));
            assertThat(httpResponse.getBody(), is(gson.toJson(expectedActuatorHealth)));
        });

        And("^alfresco is healthy$", () -> {
            alfrescoNotificationStatus = alfrescoHealthCheckSampleResponse();
            wiremock.stubFor(get(urlEqualTo(alfrescoHealthEndpoint))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(gson.toJson(alfrescoNotificationStatus))));
        });

        But("^alfresco is not healthy$", () -> wiremock.stubFor(get(urlEqualTo(alfrescoHealthEndpoint)).willReturn(aResponse().withFault(EMPTY_RESPONSE))));

        When("^I request the health of the PO Alfresco Proxy API$", () -> healthCheckResponseEntity = Unirest.get(healthCheckEndpoint()).asString());

        Then("^(?:a|an) (?:stable|unstable) response per the JSON \"([^\"]*)\" is returned$", (String filename) -> {
            final String jsonFile = jsonReader.readFile(filename)
                    .replace("${time_stamp}", timestamp.toString());
            final JsonObject healthCheckResponse = JsonParser.parseString(jsonFile).getAsJsonObject();
            final String expectedResponse = gson.toJson(healthCheckResponse);

            assertThat(healthCheckResponseEntity.getStatus(), is(HttpStatus.OK.value()));
            assertTrue(healthCheckResponseEntity.getHeaders().containsKey("Content-Type"));
            assertThat(healthCheckResponseEntity.getHeaders().get("Content-Type"), hasItem(contentType.toString()));
            assertThat(healthCheckResponseEntity.getBody(), is(expectedResponse));

            wiremock.verify(getRequestedFor(urlEqualTo(alfrescoHealthEndpoint)));
        });
    }
}