package uk.gov.gsi.justice.po.alfresco.proxy.ioc;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.gsi.justice.po.alfresco.proxy.provider.GsonProvider;

@Configuration
public class AppConfig {
    @Bean
    public Gson provideGson() {
        final GsonProvider gsonProvider = new GsonProvider();

        return gsonProvider.getGson();
    }
}
