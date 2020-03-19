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
import uk.gov.gsi.justice.po.alfresco.proxy.dto.Dependencies;
import uk.gov.gsi.justice.po.alfresco.proxy.dto.HealthCheckResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@DirtiesContext
public class HealthCheckSteps extends AbstractSteps implements En {
    public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options()
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

            alfrescoNotificationStatus = alfrescoHealthCheckSampleResponse();
            wiremock.stubFor(get(urlEqualTo(alfrescoHealthEndpoint))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(gson.toJson(alfrescoNotificationStatus))));
        });

        After(() -> {
            wiremock.shutdown();
            SECONDS.sleep(2);
        });

        Given("^the application is running$", () -> {
            final String statusUp = "expectations/actuator_health.json";
            final String jsonFile = jsonReader.readFile(statusUp);
            final JsonObject expectedActuatorHealth = JsonParser.parseString(jsonFile).getAsJsonObject();

            final HttpResponse<String> httpResponse = Unirest.get(statusEndpoint()).asString();

            assertThat(httpResponse.getStatus(), is(HttpStatus.OK.value()));
            assertThat(httpResponse.getBody(), is(gson.toJson(expectedActuatorHealth)));
        });

        And("^alfresco is healthy$", () -> {
            final HttpResponse<String> response = Unirest.get(alfrescoBaseUrl + alfrescoHealthEndpoint)
                    .asString();
            assertThat(response.getStatus(), is(200));
            assertThat(response.getBody(), is(gson.toJson(alfrescoNotificationStatus)));
        });

        When("^I request it's health$", () -> {
            healthCheckResponseEntity = Unirest.get(healthCheckEndpoint()).asString();
        });

        Then("a JSON response per {string} should be returned", (String filename) -> {
            final String statusText = "OK";
            final Dependencies dependencies = new Dependencies(new JsonObject(), new JsonObject());
            final HealthCheckResponse healthCheckResponse = new HealthCheckResponse(serviceName, statusText, dependencies, timestamp);
            final String expectedResponse = gson.toJson(healthCheckResponse);

            assertThat(healthCheckResponseEntity.getStatus(), is(HttpStatus.OK.value()));
            assertTrue(healthCheckResponseEntity.getHeaders().containsKey("Content-Type"));
            assertThat(healthCheckResponseEntity.getHeaders().get("Content-Type"), hasItem(contentType.toString()));
            assertThat(healthCheckResponseEntity.getBody(), is(expectedResponse));

            wiremock.verify(1, getRequestedFor(urlEqualTo(alfrescoHealthEndpoint)));
        });
    }
}