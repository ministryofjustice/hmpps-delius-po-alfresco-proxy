package uk.gov.gsi.justice.alfresco.proxy.bdd.stepdefs;

import io.cucumber.java8.En;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.InputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AntiVirusSteps extends AbstractSteps implements En {
    private String filename;
    private String filePath;
    private String uploadPath;
    @SuppressWarnings("rawtypes")
    private HttpResponse httpResponse;

    public AntiVirusSteps() {
        Given("^I have a virus compromised document \"([^\"]*)\" to upload$", (String filename) -> {
            this.filename = filename;
            this.filePath = "documents/" + filename;
        });

        When("^I call \"([^\"]*)\" to upload the document$", (String path) -> {
            this.uploadPath = path;
            final InputStream inputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(this.filePath);

            httpResponse = Unirest.post(baseUrl + path)
                    .field("upload", inputStream, this.filename)
                    .field("CRN", "X030927")
                    .asEmpty();
        });

        Then("^I should receive a response with status code \"([^\"]*)\"$", (Integer expectedStatusCode) -> {
            assertThat(httpResponse.getStatus(), is(expectedStatusCode));
            world.getWireMockServer().verify(0, postRequestedFor(urlEqualTo(this.uploadPath)));
        });
    }
}