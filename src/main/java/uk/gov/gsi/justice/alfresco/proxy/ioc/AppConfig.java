package uk.gov.gsi.justice.alfresco.proxy.ioc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import uk.gov.gsi.justice.alfresco.proxy.av.AntivirusScanner;
import uk.gov.gsi.justice.alfresco.proxy.service.OAuthRequestFilter;
import uk.gov.gsi.justice.alfresco.proxy.utils.PropertyResolver;

import java.security.KeyStore;
import java.security.cert.Certificate;

@Configuration
public class AppConfig {
    @Value("${spg.alfresco.proxy.clamav.address}")
    private String clamAVAddress;

    @Value("${spg.alfresco.proxy.clamav.port}")
    private int clamAVPort;

    @Value("${spg.alfresco.proxy.clamav.timeout}")
    private int clamAVTimeout;

    @Bean
    public PropertyResolver providePropertyResolver() {
        return new PropertyResolver();
    }

    @Bean(name = "antivirusScanner")
    public AntivirusScanner provideAntivirusScanner() {
        return new AntivirusScanner(clamAVAddress, clamAVPort, clamAVTimeout);
    }

    @Bean
    public KeyStore provideKeyStore() throws Exception {
        return null;
    }
}