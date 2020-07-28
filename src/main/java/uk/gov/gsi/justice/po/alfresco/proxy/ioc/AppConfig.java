package uk.gov.gsi.justice.po.alfresco.proxy.ioc;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.gov.gsi.justice.po.alfresco.proxy.http.CxfClientInterface;
import uk.gov.gsi.justice.po.alfresco.proxy.http.CxfHttpInterface;
import uk.gov.gsi.justice.po.alfresco.proxy.http.RestClient;
import uk.gov.gsi.justice.po.alfresco.proxy.provider.GsonProvider;
import uk.gov.gsi.justice.po.alfresco.proxy.service.CxfServerHandler;

import javax.inject.Inject;

import static java.util.Collections.singletonList;

@Configuration
public class AppConfig {

    @Inject
    private Bus bus;

    @Bean
    public Gson provideGson() {
        final GsonProvider gsonProvider = new GsonProvider();

        return gsonProvider.getGson();
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
    public CxfClientInterface provideCxfClient() {
        return JAXRSClientFactory.create("http://localhost:8080", CxfClientInterface.class);
    }

    @Bean
    public CxfServerHandler provideCxfServerInterface() {
        return new CxfServerHandler(provideCxfClient());
    }

    @Bean
    public Server provideCxfRsServer() {
        final JAXRSServerFactoryBean serverFactoryBean = new JAXRSServerFactoryBean();
        serverFactoryBean.setBus(bus);
        serverFactoryBean.setServiceBeans(singletonList(provideCxfServerInterface()));
        return serverFactoryBean.create();
    }

    @Bean
    public RestClient provideRestClient() {
        return provideRetrofit().create(RestClient.class);
    }
}
