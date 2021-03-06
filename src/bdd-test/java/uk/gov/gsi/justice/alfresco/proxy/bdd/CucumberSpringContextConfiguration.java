package uk.gov.gsi.justice.alfresco.proxy.bdd;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.gsi.justice.alfresco.proxy.bdd.ioc.TestConfig;
import uk.gov.gsi.justice.alfresco.proxy.ApplicationBootstrap;
import uk.gov.gsi.justice.alfresco.proxy.bdd.util.World;
import uk.gov.gsi.justice.alfresco.proxy.ioc.AppConfig;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Class to use spring application context while running cucumber
 */
@SpringBootTest(properties = {"application.name=SPG Alfresco Proxy", "alfresco.base.url=http://localhost:6067", "alfresco.health.endpoint=/afresco/s/admin-spg/healthcheck"},
        classes = {AppConfig.class, TestConfig.class, ApplicationBootstrap.class})
public class CucumberSpringContextConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(CucumberSpringContextConfiguration.class);

    private final World world = World.INSTANCE;

    /**
     * Need this method so the cucumber will recognize this class as glue and load spring context configuration
     */
    @Before
    public void setUp() throws Exception {
        LOG.info("-------------- Spring Context Initialized For Executing Cucumber Tests --------------");
        world.getWireMockServer().start();
        SECONDS.sleep(2);
    }

    @After
    public void cleanUp() throws Exception {
        world.getWireMockServer().shutdown();
        SECONDS.sleep(2);
    }
}
