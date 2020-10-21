package uk.gov.gsi.justice.alfresco.proxy.utils;

import javax.inject.Inject;
import org.springframework.core.env.Environment;

public class PropertyResolver {

  @Inject private Environment environment;

  public String getProperty(final String propertyValue) {
    return environment.getProperty(propertyValue);
  }
}
