package uk.gov.gsi.justice.alfresco.proxy.ioc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.gsi.justice.alfresco.proxy.utils.PropertyResolver;

@Configuration
public class AppConfig {
    @Bean
    public PropertyResolver providePropertyResolver() {
        return new PropertyResolver();
    }
}