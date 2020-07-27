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
    private final String cxfPath = "/cxf/test";

    public ProxyToBackendSteps() {
        Given("^a running backend$", () -> {
            world.getWireMockServer().stubFor(get(urlEqualTo(baseUrl() + cxfPath))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(gson.toJson(alfrescoNotificationStatus))));
        });

        When("^I request data from the backend$", () -> {
            sendRequest(cxfPath);
        });

        Then("^a response should be returned$", () -> {
            assertThat(world.getResponseEntity().getStatus(), is(HttpStatus.OK.value()));
            assertTrue(world.getResponseEntity().getHeaders().containsKey("Content-Type"));
            assertThat(world.getResponseEntity().getHeaders().get("Content-Type"), hasItem(contentType.toString()));
            assertNotNull(world.getResponseEntity().getBody());

            world.getWireMockServer().verify(getRequestedFor(urlEqualTo(cxfPath)));
        });
    }
}
