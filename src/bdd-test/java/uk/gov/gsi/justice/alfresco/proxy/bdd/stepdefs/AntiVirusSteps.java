package uk.gov.gsi.justice.alfresco.proxy.bdd.stepdefs;

import io.cucumber.java8.En;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import javax.ws.rs.core.Response;
import java.io.File;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AntiVirusSteps extends AbstractSteps implements En {
    private String filePath;
    private String uploadPath;
    @SuppressWarnings("rawtypes")
    private Response httpResponse;

    public AntiVirusSteps() {
        Before(this::startClamAV);

        Given("^I have a virus compromised document \"([^\"]*)\" to upload$", (final String filename) -> {
            this.filePath = "documents/" + filename;
        });

        When("^I call \"([^\"]*)\" to upload the document$", (final String path) -> {
            this.uploadPath = path;

            final File file = new File(getClass().getClassLoader().getResource(filePath).getFile());

            final FileDataBodyPart filePart = new FileDataBodyPart("filedata", file);
            final MultiPart multiPart = new FormDataMultiPart().field("CRN", "X030927").bodyPart(filePart);

            multiPart.setMediaType(MULTIPART_FORM_DATA_TYPE);

            httpResponse = webTarget.path(path)
                    .register(MultiPartFeature.class)
                    .request(APPLICATION_JSON_TYPE)
                    .headers(headers)
                    .post(entity(multiPart, multiPart.getMediaType()));
        });

        Then("^I should receive a response with status code \"([^\"]*)\"$", (final Integer expectedStatusCode) -> {
            assertThat(httpResponse.getStatus(), is(expectedStatusCode));
            world.getWireMockServer().verify(0, postRequestedFor(urlEqualTo(this.uploadPath)));
        });
    }
}