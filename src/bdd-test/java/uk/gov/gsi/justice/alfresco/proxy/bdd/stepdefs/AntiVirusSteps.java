package uk.gov.gsi.justice.alfresco.proxy.bdd.stepdefs;

import io.cucumber.java8.En;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AntiVirusSteps extends AbstractSteps implements En {
    private String fileName;
    @SuppressWarnings("rawtypes")
    private HttpResponse httpResponse;

    public AntiVirusSteps() {
        Given("^I have a virus compromised document \"([^\"]*)\" to upload$", (String filename) -> {
            fileName = "documents/" + filename;
        });

        When("^I call \"([^\"]*)\" to upload the document$", (String path) -> {
            final InputStream inputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(fileName);

            httpResponse = Unirest.post(baseUrl + path)
                    .field("upload", inputStream, "X030927")
                    .asEmpty();
        });

        Then("^I should receive a response with status code \"([^\"]*)\"$", (String expectedStatusCode) -> {
            assertThat(httpResponse.getStatus(), is(expectedStatusCode));
        });
    }
}