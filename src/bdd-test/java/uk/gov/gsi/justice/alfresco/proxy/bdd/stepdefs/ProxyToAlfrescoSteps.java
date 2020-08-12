package uk.gov.gsi.justice.alfresco.proxy.bdd.stepdefs;

import io.cucumber.java8.En;
import uk.gov.gsi.justice.alfresco.proxy.bdd.util.StubPath;

public class ProxyToAlfrescoSteps extends AbstractSteps implements En {
    public ProxyToAlfrescoSteps() {
        Given("^a document is available at \"([^\"]*)\"$", this::createGetStub);
        Given("^I want to reserve a document from alfresco$", () -> createPostStub(StubPath.RESERVE_PATH.name()));
        Given("^I want to upload and release document to alfresco$", () -> createPostStub(StubPath.UPLOAD_AND_RELEASE_PATH.name()));
        Given("^I want to upload a new document to alfresco$", () -> createPostStub(StubPath.UPLOAD_NEW_PATH.name()));
        Given("^I want to release document from alfresco$", () -> createPutStub(StubPath.RELEASE_PATH.name()));
        Given("^I want to delete document from alfresco$", () -> createDeleteStub(StubPath.DELETE_PATH.name()));
        Given("^I want to delete multiple documents from alfresco by CRN$", () -> createDeleteStub(StubPath.DELETE_ALL_PATH.name()));
        Given("^I want to hard delete document from alfresco$", () -> createDeleteStub(StubPath.DELETE_HARD_PATH.name()));
        Given("^I want to delete multiple documents from alfresco$", () -> createPostStub(StubPath.DELETE_MULTIPLE_PATH.name()));
        Given("^I want to move document$", () -> createPostStub(StubPath.MOVE_PATH.name()));
        Given("^I want to undelete a document from alfresco$", () -> createPostStub(StubPath.UNDELETE_PATH.name()));
        Given("^I want to update document metadata$", () -> createPostStub(StubPath.UPDATE_METADATA_PATH.name()));
        Given("^I want to lock document in alfresco$", () -> createPutStub(StubPath.LOCK_PATH.name()));
        Given("^I want to get alfresco notification status$", () -> createGetStub(StubPath.NOTIFICATION_STATUS_PATH.name()));

        When("^I request \"([^\"]*)\" from alfresco$", (final String path) -> sendGetRequest(path));
        When("^I search document using \"([^\"]*)\" from alfresco$", this::sendGetRequest);
        When("^I fetch document using \"([^\"]*)\" from alfresco$", (final String path) -> sendGetRequest(path));
        When("^I fetch document stream using \"([^\"]*)\" from alfresco$", (final String path) -> sendGetRequest(path));

        When("^I fetch and reserve document using \"([^\"]*)\" from alfresco$", (final String path) -> sendPostRequest(path));
        When("^I reserve the document using \"([^\"]*)\" from alfresco$", (final String path) -> sendPostRequest(path));
        When("^I upload new document using \"([^\"]*)\"$", (final String path) -> sendPostRequest(path));
        When("^I delete multiple documents using \"([^\"]*)\" from alfresco$", (final String path) -> sendPostRequest(path));
        When("^I move the document using \"([^\"]*)\" from alfresco$", (final String path) -> sendPostRequest(path));
        When("^I undelete the document using \"([^\"]*)\" from alfresco$", (final String path) -> sendPostRequest(path));
        When("^I update the document metadata \"([^\"]*)\" from alfresco$", (final String path) -> sendPostRequest(path));
        When("^I upload a document using \"([^\"]*)\"$", (final String path) -> sendPostRequest(path));

        When("^I delete document using \"([^\"]*)\" from alfresco$", (final String path) -> sendDeleteRequest(path));
        When("^I delete all documents for CRN using \"([^\"]*)\" from alfresco$",(final String path) -> sendDeleteRequest(path));

        When("^I release document using \"([^\"]*)\" from alfresco$", (final String path) -> sendPutRequest(path));
        When("^I lock document using \"([^\"]*)\" from alfresco$", (final String path) -> sendPutRequest(path));

        Then("^the document should be successfully uploaded to alfresco$", this::assertResponse);
        Then("^the document should be successfully uploaded and released$",this::assertResponse);
        Then("^a successful response should be returned$", this::assertResponse);
    }
}
