package uk.gov.gsi.justice.alfresco.proxy.bdd.ioc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import uk.gov.gsi.justice.alfresco.proxy.av.AntivirusScanner;
import uk.gov.gsi.justice.alfresco.proxy.service.OAuthRequestFilter;
import uk.gov.gsi.justice.alfresco.proxy.utils.TimestampProvider;

import javax.annotation.PreDestroy;
import java.time.Duration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {
    private final String clamAVImage = "mkodockx/docker-clamav:alpine";
    private final int clamAVPort = 3310;

    @SuppressWarnings("rawtypes")
    private final GenericContainer clamAV = new GenericContainer<>(clamAVImage)
            .withExposedPorts(clamAVPort)
            .waitingFor(Wait.forListeningPort()
                    .withStartupTimeout(Duration.ofMinutes(5)));

    public TestConfig() {
        clamAV.start();
    }

    @PreDestroy
    public void cleanUp() {
        clamAV.stop();
    }

    @Bean(name = "antivirusScanner")
    @Primary
    public AntivirusScanner provideAntivirusScanner() {
        final String clamAVAddress = "localhost";
        final int clamAVTimeout = 60000;
        return new AntivirusScanner(clamAVAddress, clamAV.getFirstMappedPort(), clamAVTimeout);
    }

    @Bean(name = "authorizationFilter")
    @Primary
    public OAuthRequestFilter provideOAuthRequestFilter() {
        final String oauthProtocol = "http";
        return new OAuthRequestFilter(oauthProtocol);
    }

    @Bean
    @Primary
    public TimestampProvider provideTimestampProvider() {
        return mock(TimestampProvider.class);
    }
}