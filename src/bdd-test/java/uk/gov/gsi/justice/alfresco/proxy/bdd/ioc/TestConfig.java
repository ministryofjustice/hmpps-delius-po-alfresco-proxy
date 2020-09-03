package uk.gov.gsi.justice.alfresco.proxy.bdd.ioc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import uk.gov.gsi.justice.alfresco.proxy.av.AntivirusScanner;
import uk.gov.gsi.justice.alfresco.proxy.bdd.security.KeyStoreGenerator;
import uk.gov.gsi.justice.alfresco.proxy.utils.TimestampProvider;

import javax.annotation.PreDestroy;
import java.security.KeyStore;
import java.time.Duration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {
    private final String clamAVImage = "quay.io/ukhomeofficedigital/clamav:latest";
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

    @Bean
    @Primary
    public TimestampProvider provideTimestampProvider() {
        return mock(TimestampProvider.class);
    }

    @Bean
    @Primary
    public KeyStore provideKeyStore() throws Exception {
        return new KeyStoreGenerator().getKeyStore();
    }
}