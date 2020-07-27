package uk.gov.gsi.justice.po.alfresco.proxy.bdd.stepdefs;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.boot.web.server.LocalServerPort;
import uk.gov.gsi.justice.po.alfresco.proxy.AbstractBaseTest;
import uk.gov.gsi.justice.po.alfresco.proxy.bdd.util.World;

public abstract class AbstractSteps extends AbstractBaseTest {
    @LocalServerPort
    private int port;

    protected World world = World.INSTANCE;

    protected String baseUrl() {
        return "http://localhost:" + port;
    }

    protected void sendRequest(final String path) {
        final HttpResponse<String> healthCheckResponseEntity = Unirest.get(baseUrl() + path).asString();
        world.setResponseEntity(healthCheckResponseEntity);
    }
}
