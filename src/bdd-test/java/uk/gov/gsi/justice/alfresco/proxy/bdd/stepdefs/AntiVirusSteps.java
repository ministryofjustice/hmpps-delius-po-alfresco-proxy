package uk.gov.gsi.justice.alfresco.proxy.bdd.stepdefs;

import io.cucumber.java8.En;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import uk.gov.gsi.justice.alfresco.proxy.bdd.util.ThrowingFunction;

import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URL;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class AntiVirusSteps extends AbstractSteps implements En {
    private File file;
    private String uploadPath;
    private Response httpResponse;

    public AntiVirusSteps() {
        Before(() -> {
            startClamAV();

            when(timestampProvider.getTimestamp()).thenReturn(timestamp);

            System.out.println("============================== Inside AntiVirusSteps Before ==============================");
            System.out.println("Host: " + clamAvConnectionParametersProvider.host());
            System.out.println("Port: " + clamAvConnectionParametersProvider.port());
            System.out.println("===============================================================================");
        });

        Given("^I have a virus compromised document \"([^\"]*)\" to upload$", (final String filename) -> {
            final String fileName = "documents/" + filename;
            file = getFileFromResource(fileName);
        });

        When("^I call \"([^\"]*)\" to upload the document$", (final String path) -> {
            this.uploadPath = path;

            final FileDataBodyPart filePart = new FileDataBodyPart("filedata", file);
            final MultiPart multiPart = new FormDataMultiPart().field("CRN", "X030927").bodyPart(filePart);
            multiPart.setMediaType(MULTIPART_FORM_DATA_TYPE);

            httpResponse = webTarget.path(path)
                    .register(MultiPartFeature.class)
                    .request(APPLICATION_JSON_TYPE)
                    .headers(headers)
                    .post(entity(multiPart, multiPart.getMediaType()));

            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~> http response code " + httpResponse.getStatus());
        });

        Then("^I should receive a response with status code \"([^\"]*)\"$", (final Integer expectedStatusCode) -> {
            assertThat(httpResponse.getStatus(), is(expectedStatusCode));
            world.getWireMockServer().verify(0, postRequestedFor(urlEqualTo(this.uploadPath)));
        });
    }

    private File getFileFromResource(String fileName) {
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource(fileName);

        return Optional.ofNullable(resource)
                .map(ThrowingFunction.unchecked(x -> new File(x.toURI())))
                .orElseThrow(() ->new IllegalArgumentException("file not found! " + fileName));
    }
}