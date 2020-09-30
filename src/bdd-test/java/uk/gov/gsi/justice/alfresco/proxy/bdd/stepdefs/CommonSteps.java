package uk.gov.gsi.justice.alfresco.proxy.bdd.stepdefs;

import io.cucumber.java8.En;

public class CommonSteps extends AbstractSteps implements En {
    public CommonSteps() {
        And("^ClamAV is healthy$", this::startClamAV);

        And("^ClamAV is offline$", this::stopClamAV);
    }
}
