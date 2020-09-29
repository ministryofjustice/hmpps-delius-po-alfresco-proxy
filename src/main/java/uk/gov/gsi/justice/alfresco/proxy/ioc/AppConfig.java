package uk.gov.gsi.justice.alfresco.proxy.ioc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.gsi.justice.alfresco.proxy.av.AntivirusScanner;
import uk.gov.gsi.justice.alfresco.proxy.utils.*;

import java.security.KeyStore;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Configuration
public class AppConfig {
    @Value("${spg.alfresco.proxy.clamav.address}")
    private String clamAVAddress;

    @Value("${spg.alfresco.proxy.clamav.port}")
    private int clamAVPort;

    @Value("${spg.alfresco.proxy.clamav.timeout}")
    private int clamAVTimeout;

    private final ClamAvConnectionParametersProvider clamAvConnectionParametersProvider =
            new DefaultClamAvConnectionParametersProvider(clamAVAddress, clamAVPort, clamAVTimeout);

    @Bean
    public PropertyResolver providePropertyResolver() {
        return new PropertyResolver();
    }

    @Bean(name = "antivirusScanner")
    public AntivirusScanner provideAntivirusScanner() {
        return new AntivirusScanner(clamAvConnectionParametersProvider);
    }

    @Bean
    public ClamAvConnectionParametersProvider provideClamAvConnectionParametersProvider() {
        return clamAvConnectionParametersProvider;
    }

    @Bean
    public KeyStore provideKeyStore() throws Exception {
        return null;
    }

    @Bean
    public TimestampProvider provideTimestampProvider() {
        return new DefaultTimestampProvider();
    }

    @Bean
    public ObjectMapper provideObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}