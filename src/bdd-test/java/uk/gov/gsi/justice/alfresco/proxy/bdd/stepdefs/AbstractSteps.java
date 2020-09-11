package uk.gov.gsi.justice.alfresco.proxy.bdd.stepdefs;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.beans.factory.annotation.Value;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import uk.gov.gsi.justice.alfresco.proxy.AbstractBaseTest;
import uk.gov.gsi.justice.alfresco.proxy.bdd.security.WebTargetBuilder;
import uk.gov.gsi.justice.alfresco.proxy.bdd.util.World;

import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.*;
import static org.glassfish.jersey.client.ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.gsi.justice.alfresco.proxy.bdd.util.HttpHeadersMapProvider.getMultivaluedMap;
import static uk.gov.gsi.justice.alfresco.proxy.bdd.util.World.INSTANCE;

public abstract class AbstractSteps extends AbstractBaseTest {
    @Value("${spg.alfresco.proxy.inbound.address}")
    protected String baseUrl;

    @Inject
    private WebTargetBuilder webTargetBuilder;

    protected World world = INSTANCE;

    private String path;

    protected void sendGetRequest(final String path) throws Exception {
        final Response response = webTargetBuilder.provideWebTarget()
                .path(path)
                .request(APPLICATION_JSON_TYPE)
                .headers(getMultivaluedMap())
                .get();
        world.setResponse(response);
    }

    protected void sendPostRequest(final String path) throws Exception {
        final MultiPart multiPart = new MultiPart().bodyPart(new BodyPart("", MULTIPART_FORM_DATA_TYPE));
        multiPart.setMediaType(MULTIPART_FORM_DATA_TYPE);

        final Response response = webTargetBuilder.provideWebTarget()
                .path(path)
                .register(MultiPartFeature.class)
                .request(APPLICATION_JSON_TYPE)
                .headers(getMultivaluedMap())
                .post(entity(multiPart, multiPart.getMediaType()));

        world.setResponse(response);
    }

    protected void sendMultideletePostRequest(final String path) throws Exception {
        final WebTarget webTarget = webTargetBuilder.provideWebTarget();
        final Response response = webTarget
                .register(JacksonJsonProvider.class)
                .path(path)
                .request(APPLICATION_JSON_TYPE)
                .headers(getMultivaluedMap())
                .post(entity(ImmutableMap.of("DOCUMENT_IDS", "1,2,3"), APPLICATION_JSON));
        world.setResponse(response);
    }

    protected void sendPutRequest(final String path) throws Exception {
        final WebTarget webTarget = webTargetBuilder.provideWebTarget();
        webTarget.property(SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
        final Response response = webTarget
                .path(path)
                .request(APPLICATION_JSON_TYPE)
                .headers(getMultivaluedMap())
                .put(null);
        world.setResponse(response);
    }

    protected void sendDeleteRequest(final String path) throws Exception {
        final Response response = webTargetBuilder.provideWebTarget()
                .path(path)
                .request(APPLICATION_JSON_TYPE)
                .headers(getMultivaluedMap())
                .delete();
        world.setResponse(response);
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
        assertThat(world.getResponse().getStatus(), is(200));
        assertTrue(world.getResponse().getHeaders().containsKey("Content-Type"));
        assertThat(world.getResponse().getHeaders().get("Content-Type"), hasItem("application/json"));
        assertNotNull(world.getResponse().getEntity());

        world.getWireMockServer().verify(anyRequestedFor(urlEqualTo(path)));
    }

    public void setPath(final String path) {
        this.path = path;
    }
}