package uk.gov.gsi.justice.po.alfresco.proxy.bdd.ioc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import uk.gov.gsi.justice.po.alfresco.proxy.provider.TimestampProvider;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {
    @Bean
    @Primary
    public TimestampProvider provideTimestampProvider() {
        return mock(TimestampProvider.class);
    }
}
