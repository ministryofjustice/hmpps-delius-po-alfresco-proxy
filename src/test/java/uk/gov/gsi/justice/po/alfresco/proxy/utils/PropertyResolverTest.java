package uk.gov.gsi.justice.po.alfresco.proxy.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.gsi.justice.po.alfresco.proxy.bdd.ioc.TestConfig;
import uk.gov.gsi.justice.po.alfresco.proxy.ioc.AppConfig;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"alfresco.base.url=http://my-test-url", "application.name=test-name"}, classes = {AppConfig.class, TestConfig.class})
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