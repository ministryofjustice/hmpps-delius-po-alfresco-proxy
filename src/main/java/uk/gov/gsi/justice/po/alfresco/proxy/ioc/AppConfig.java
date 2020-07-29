package uk.gov.gsi.justice.po.alfresco.proxy.ioc;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.gov.gsi.justice.po.alfresco.proxy.cxf.client.CxfClientInterface;
import uk.gov.gsi.justice.po.alfresco.proxy.cxf.provider.JsonToMapBodyReader;
import uk.gov.gsi.justice.po.alfresco.proxy.cxf.provider.MapToJsonBodyWriter;
import uk.gov.gsi.justice.po.alfresco.proxy.cxf.server.CxfServerHandler;
import uk.gov.gsi.justice.po.alfresco.proxy.http.RestClient;
import uk.gov.gsi.justice.po.alfresco.proxy.provider.GsonProvider;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
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

    @Bean(name = "CxfProviders")
    public List<Object> provideCxfProviders() {
        return asList(new JSONProvider(), new JAXBElementProvider(), new JsonToMapBodyReader(), new MapToJsonBodyWriter());
    }

    @Bean
    public CxfClientInterface provideCxfClient() {
        return JAXRSClientFactory.create("http://localhost:6067", CxfClientInterface.class, singletonList(new JsonToMapBodyReader()));
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
        serverFactoryBean.setProviders(singletonList(new JsonToMapBodyReader()));
        return serverFactoryBean.create();
    }

    @Bean
    public RestClient provideRestClient() {
        return provideRetrofit().create(RestClient.class);
    }
}
