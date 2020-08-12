package uk.gov.gsi.justice.alfresco.proxy.utils;

import org.springframework.core.env.Environment;

import javax.inject.Inject;

public class PropertyResolver {

    @Inject
    private Environment environment;

    public String getProperty(final String propertyValue){
        return environment.getProperty(propertyValue);
    }
}
