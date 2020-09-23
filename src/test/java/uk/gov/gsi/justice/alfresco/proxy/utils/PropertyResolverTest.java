package uk.gov.gsi.justice.alfresco.proxy.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.gsi.justice.alfresco.proxy.ioc.AppConfig;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        properties = {"alfresco.base.url=http://my-test-url", "application.name=test-name"},
        classes = {AppConfig.class}
)
public class PropertyResolverTest {
    @Inject
    private PropertyResolver propertyResolver;

    @Test
    public void shouldGetApplicationProperties() {
        final String appName = propertyResolver.getProperty("application.name");
        final String baseUrlName = propertyResolver.getProperty("alfresco.base.url");

        assertThat(appName, is("test-name"));
        assertThat(baseUrlName, is("http://my-test-url"));
    }
}