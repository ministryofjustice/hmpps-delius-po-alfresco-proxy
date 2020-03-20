package uk.gov.gsi.justice.po.alfresco.proxy.bdd.stepdefs;

import org.springframework.boot.web.server.LocalServerPort;
import uk.gov.gsi.justice.po.alfresco.proxy.AbstractBaseTest;

public abstract class AbstractSteps extends AbstractBaseTest {
    private final String serverUrl = "http://localhost";
    @LocalServerPort
    private int port;

    protected String healthCheckEndpoint() {
        return serverUrl + ":" + port + apiHealthEndpoint;
    }

    protected String statusEndpoint() {
        return serverUrl + ":" + port + "/actuator/health";
    }
}
