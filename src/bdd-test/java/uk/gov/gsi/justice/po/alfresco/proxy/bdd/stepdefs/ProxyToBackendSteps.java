package uk.gov.gsi.justice.po.alfresco.proxy.bdd.stepdefs;

import io.cucumber.java8.En;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ProxyToBackendSteps extends AbstractSteps implements En {
    private String requestPath;

    public ProxyToBackendSteps() {
        Given("^a document is available on the backend at \"([^\"]*)\"$", (final String path) -> {
            requestPath = path;
            world.getWireMockServer().stubFor(get(urlEqualTo(baseUrl() + path))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(gson.toJson(alfrescoNotificationStatus))));
        });

        When("^I request \"([^\"]*)\" from the backend$", (final String path) -> sendRequest(path));

        Then("^a successful response should be returned$", () -> {
            assertThat(world.getResponseEntity().getStatus(), is(HttpStatus.OK.value()));
            assertTrue(world.getResponseEntity().getHeaders().containsKey("Content-Type"));
            assertThat(world.getResponseEntity().getHeaders().get("Content-Type"), hasItem(contentType.toString()));
            assertNotNull(world.getResponseEntity().getBody());

            world.getWireMockServer().verify(getRequestedFor(urlEqualTo(requestPath)));
        });

        When("^I post data to \"([^\"]*)\"$", (final String path) -> {
        });

        Then("^my data should be successfully delivered to the backend$", () -> {
        });
        Given("^a running backend$", () -> {
        });
    }
}
