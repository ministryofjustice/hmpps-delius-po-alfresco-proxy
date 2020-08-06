package uk.gov.gsi.justice.po.alfresco.proxy.ioc;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.gov.gsi.justice.po.alfresco.proxy.http.RestClient;
import uk.gov.gsi.justice.po.alfresco.proxy.provider.GsonProvider;
import uk.gov.gsi.justice.po.alfresco.proxy.utils.PropertyResolver;

@Configuration
public class AppConfig {

    @Bean
    public Gson provideGson() {
        final GsonProvider gsonProvider = new GsonProvider();

        return gsonProvider.getGson();
    }

    @Bean
    public PropertyResolver providePropertyResolver() {
        return new PropertyResolver();
    }

    @Bean
    public Retrofit provideRetrofit() {
        final HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BASIC);
        final OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl("http://localhost")
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .client(httpClient)
                .build();
    }


    @Bean
    public RestClient provideRestClient() {
        return provideRetrofit().create(RestClient.class);
    }
}
