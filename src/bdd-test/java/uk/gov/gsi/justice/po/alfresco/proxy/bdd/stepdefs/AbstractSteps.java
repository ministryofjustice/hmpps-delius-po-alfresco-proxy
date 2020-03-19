package uk.gov.gsi.justice.po.alfresco.proxy.bdd.stepdefs;

import org.springframework.boot.web.server.LocalServerPort;
import uk.gov.gsi.justice.po.alfresco.proxy.AbstractBaseTest;

public abstract class AbstractSteps extends AbstractBaseTest {
    private final String SERVER_URL = "http://localhost";
    @LocalServerPort
    private int port;

    protected String healthCheckEndpoint() {
        return SERVER_URL + ":" + port + "/api/healthcheck";
    }

    protected String statusEndpoint() {
        return SERVER_URL + ":" + port + "/actuator/health";
    }
}
