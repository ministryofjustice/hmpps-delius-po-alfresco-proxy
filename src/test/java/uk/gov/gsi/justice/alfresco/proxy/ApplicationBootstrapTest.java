package uk.gov.gsi.justice.alfresco.proxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.gsi.justice.alfresco.proxy.ioc.AppConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        properties = {"application.name=Alfresco-Proxy", "alfresco.base.url=http://localhost:6067", "alfresco.health.endpoint=/alfresco/service/noms-spg/notificationStatus"},
        classes = {AppConfig.class, ApplicationBootstrap.class}
)
public class ApplicationBootstrapTest {
    @Test
    public void contextLoads() {
    }
}