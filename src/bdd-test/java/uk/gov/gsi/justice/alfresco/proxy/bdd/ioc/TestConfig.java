package uk.gov.gsi.justice.alfresco.proxy.bdd.ioc;

import com.google.gson.Gson;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import uk.gov.gsi.justice.alfresco.proxy.GsonProvider;
import uk.gov.gsi.justice.alfresco.proxy.av.AntivirusScanner;
import uk.gov.gsi.justice.alfresco.proxy.bdd.security.KeyStoreGenerator;
import uk.gov.gsi.justice.alfresco.proxy.utils.ClamAvConnectionParametersProvider;
import uk.gov.gsi.justice.alfresco.proxy.utils.TimestampProvider;

import javax.annotation.PreDestroy;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.time.Duration;

import static java.net.URI.create;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static org.glassfish.jersey.client.JerseyClientBuilder.createClient;
import static org.mockito.Mockito.mock;
import static uk.gov.gsi.justice.alfresco.proxy.bdd.util.KeyStoreConfigs.*;

@Configuration
public class TestConfig {
    private final KeyStore keyStore = new KeyStoreGenerator().getKeyStore();

    private final String clamAVImage = "quay.io/ukhomeofficedigital/clamav:latest";
    private final int clamAVPort = 3310;

    @SuppressWarnings("rawtypes")
    private final GenericContainer clamAV = new GenericContainer<>(clamAVImage)
            .withExposedPorts(clamAVPort)
            .waitingFor(Wait.forListeningPort()
                    .withStartupTimeout(Duration.ofMinutes(5)));

    private final ClamAvConnectionParametersProvider clamAvConnectionParametersProvider = mock(ClamAvConnectionParametersProvider.class);

    public TestConfig() {
        clamAV.start();
    }

    @PreDestroy
    public void cleanUp() {
        clamAV.stop();
    }

    @Value("${spg.alfresco.proxy.inbound.address}")
    private String baseUrl;

    @Bean
    @SuppressWarnings("rawtypes")
    public GenericContainer provideClamAvContainer() {
        return clamAV;
    }

    @Bean
    public Gson providerGson() {
        final GsonProvider gsonProvider = new GsonProvider();
        return gsonProvider.getGson();
    }

    @Bean(name = "antivirusScanner")
    @Primary
    public AntivirusScanner provideAntivirusScanner() {
        return new AntivirusScanner(clamAvConnectionParametersProvider);
    }

    @Bean
    @Primary
    public ClamAvConnectionParametersProvider provideClamAvClientProvider() {
        return clamAvConnectionParametersProvider;
    }

    @Bean
    @Primary
    public TimestampProvider provideTimestampProvider() {
        return mock(TimestampProvider.class);
    }

    @Bean
    @Primary
    public KeyStore provideKeyStore() throws Exception {
        return keyStore;
    }

    @Bean
    public WebTarget provideWebTarget() throws Exception {
        final Client client = createClient();

        final PrivateKey privateKey = (PrivateKey) keyStore.getKey(ALIAS, PASSWORD.toCharArray());
        final String consumerSecret = printBase64Binary(privateKey.getEncoded());

        final ConsumerCredentials consumerCredentials = new ConsumerCredentials(CERTIFICATE_COMMON_NAME, consumerSecret);
        final Feature filterFeature = OAuth1ClientSupport
                .builder(consumerCredentials)
                .signatureMethod(SIGNATURE_METHOD)
                .feature()
                .build();

        return client.register(filterFeature)
                .target(create(baseUrl));
    }
}