package uk.gov.gsi.justice.po.alfresco.proxy.bdd;

import io.cucumber.java.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import uk.gov.gsi.justice.po.alfresco.proxy.ApplicationBootstrap;
import uk.gov.gsi.justice.po.alfresco.proxy.bdd.ioc.TestConfig;
import uk.gov.gsi.justice.po.alfresco.proxy.ioc.AppConfig;

/**
 * Class to use spring application context while running cucumber
 */
@SpringBootTest(properties = "alfresco.base.url=http://localhost:6067", webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {AppConfig.class, TestConfig.class, ApplicationBootstrap.class}, loader = SpringBootContextLoader.class)
public class CucumberSpringContextConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(CucumberSpringContextConfiguration.class);

    /**
     * Need this method so the cucumber will recognize this class as glue and load spring context configuration
     */
    @Before
    public void setUp() {
        LOG.info("-------------- Spring Context Initialized For Executing Cucumber Tests --------------");
    }
}
