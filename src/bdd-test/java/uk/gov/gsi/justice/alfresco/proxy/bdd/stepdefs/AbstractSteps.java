package uk.gov.gsi.justice.alfresco.proxy.bdd.stepdefs;

import com.github.tomakehurst.wiremock.client.WireMock;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.gsi.justice.alfresco.proxy.AbstractBaseTest;
import uk.gov.gsi.justice.alfresco.proxy.bdd.util.World;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.gsi.justice.alfresco.proxy.bdd.util.World.INSTANCE;

public abstract class AbstractSteps extends AbstractBaseTest {
    @Value("${spg.alfresco.proxy.inbound.address}")
    protected String baseUrl;

    protected World world = INSTANCE;

    private String path;

    protected void sendGetRequest(final String path) {
        final HttpResponse<String> responseEntity = Unirest.get(baseUrl + path).asString();
        world.setResponseEntity(responseEntity);
    }

    protected void sendPostRequest(final String path) {
        final HttpResponse<String> responseEntity = Unirest.post(baseUrl + path).asString();
        world.setResponseEntity(responseEntity);
    }

    protected void sendPutRequest(final String path) {
        final HttpResponse<String> responseEntity = Unirest.put(baseUrl + path).asString();
        world.setResponseEntity(responseEntity);
    }

    protected void sendDeleteRequest(final String path) {
        final HttpResponse<String> responseEntity = Unirest.delete(baseUrl + path).asString();
        world.setResponseEntity(responseEntity);
    }

    //Stubs
    protected void createGetStub(final String path) {
        setPath(path);
        world.getWireMockServer().stubFor(WireMock.get(urlEqualTo(path))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(alfrescoNotificationStatus))));
    }

    protected void createPostStub(final String path) {
        setPath(path);
        world.getWireMockServer().stubFor(WireMock.post(urlEqualTo(path))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(alfrescoNotificationStatus))));
    }

    protected void createPutStub(final String path) {
        setPath(path);
        world.getWireMockServer().stubFor(WireMock.put(urlEqualTo(path))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(alfrescoNotificationStatus))));
    }

    protected void createDeleteStub(final String path) {
        setPath(path);
        world.getWireMockServer().stubFor(WireMock.delete(urlEqualTo(path))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(alfrescoNotificationStatus))));
    }

    protected void assertResponse() {
        assertThat(world.getResponseEntity().getStatus(), is(200));
        assertTrue(world.getResponseEntity().getHeaders().containsKey("Content-Type"));
        assertThat(world.getResponseEntity().getHeaders().get("Content-Type"), hasItem("application/json"));
        assertNotNull(world.getResponseEntity().getBody());

        world.getWireMockServer().verify(getRequestedFor(urlEqualTo(path)));
    }

    public void setPath(final String path) {
        this.path = path;
    }
}