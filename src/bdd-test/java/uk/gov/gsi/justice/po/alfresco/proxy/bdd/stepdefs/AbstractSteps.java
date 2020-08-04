package uk.gov.gsi.justice.po.alfresco.proxy.bdd.stepdefs;

import com.github.tomakehurst.wiremock.client.WireMock;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.boot.web.server.LocalServerPort;
import uk.gov.gsi.justice.po.alfresco.proxy.AbstractBaseTest;
import uk.gov.gsi.justice.po.alfresco.proxy.bdd.util.World;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static uk.gov.gsi.justice.po.alfresco.proxy.bdd.util.World.INSTANCE;

public abstract class AbstractSteps extends AbstractBaseTest {
    @LocalServerPort
    private int port;

    protected World world = INSTANCE;

    private String tempPath;

    protected String baseUrl() {
        return "http://localhost:" + port;
    }

    protected void sendGetRequest(final String path) {
        final HttpResponse<String> responseEntity = Unirest.get(baseUrl() + path).asString();
        world.setResponseEntity(responseEntity);
    }

    protected void sendPostRequest(final String path) {
        final HttpResponse<String> responseEntity = Unirest.post(baseUrl() + path).asString();
        world.setResponseEntity(responseEntity);
    }

    protected void sendPutRequest(final String path) {
        final HttpResponse<String> responseEntity = Unirest.put(baseUrl() + path).asString();
        world.setResponseEntity(responseEntity);
    }

    protected void sendDeleteRequest(final String path) {
        final HttpResponse<String> responseEntity = Unirest.delete(baseUrl() + path).asString();
        world.setResponseEntity(responseEntity);
    }

    //Stubs
    protected void createGetStub(final String path) {
        setTempPath(path);
        world.getWireMockServer().stubFor(WireMock.get(urlEqualTo(baseUrl() + path))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(alfrescoNotificationStatus))));
    }

    protected void createPostStub(final String path) {
        setTempPath(path);
        world.getWireMockServer().stubFor(WireMock.post(urlEqualTo(baseUrl() + path))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(alfrescoNotificationStatus))));
    }

    protected void createPutStub(final String path) {
        setTempPath(path);
        world.getWireMockServer().stubFor(WireMock.put(urlEqualTo(baseUrl() + path))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(alfrescoNotificationStatus))));
    }

    protected void createDeleteStub(final String path) {
        setTempPath(path);
        world.getWireMockServer().stubFor(WireMock.delete(urlEqualTo(baseUrl() + path))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(gson.toJson(alfrescoNotificationStatus))));
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(final String tempPath) {
        this.tempPath = tempPath;
    }
}
