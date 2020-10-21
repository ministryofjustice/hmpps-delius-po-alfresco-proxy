package uk.gov.gsi.justice.alfresco.proxy.ioc;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.gsi.justice.alfresco.proxy.utils.*;

@Configuration
public class AppConfig {
  @Value("${spg.alfresco.proxy.clamav.address}")
  private String clamAVAddress;

  @Value("${spg.alfresco.proxy.clamav.port}")
  private int clamAVPort;

  @Value("${spg.alfresco.proxy.clamav.timeout}")
  private int clamAVTimeout;

  @Inject private PropertyResolver propertyResolver;

  private final ClamAvConnectionParametersProvider clamAvConnectionParametersProvider =
      new DefaultClamAvConnectionParametersProvider(clamAVAddress, clamAVPort, clamAVTimeout);

  @Bean
  public PropertyResolver providePropertyResolver() {
    return new PropertyResolver();
  }

  @Bean
  public ClamAvConnectionParametersProvider provideClamAvConnectionParametersProvider() {
    return clamAvConnectionParametersProvider;
  }

  @Bean
  public KeyStore provideKeyStore() throws Exception {
    final FileInputStream is = new FileInputStream("/opt/app/truststore/oneTrustKeystore.jks");

    final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
    keystore.load(
        is, propertyResolver.getProperty("spg.alfresco.proxy.trustStore.password").toCharArray());

    return keystore;
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
