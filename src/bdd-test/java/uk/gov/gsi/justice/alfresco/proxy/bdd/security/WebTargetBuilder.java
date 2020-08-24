package uk.gov.gsi.justice.alfresco.proxy.bdd.security;

import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import java.security.KeyStore;
import java.security.PrivateKey;

import static java.net.URI.create;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static org.glassfish.jersey.client.JerseyClientBuilder.createClient;
import static uk.gov.gsi.justice.alfresco.proxy.bdd.util.KeyStoreConfigs.*;

@Named
public class WebTargetBuilder {

    @Value("${spg.alfresco.proxy.inbound.address}")
    protected String baseUrl;

    @Inject
    private KeyStore keyStore;

    public WebTarget provideWebTarget() throws Exception {
        final Client client = buildJerseyClient();
        return client.target(create(baseUrl));
    }

    private Client buildJerseyClient() throws Exception {
        final Client client = createClient();

        final PrivateKey privateKey = (PrivateKey) keyStore.getKey(ALIAS, PASSWORD.toCharArray());
        final String consumerSecret = printBase64Binary(privateKey.getEncoded());

        final ConsumerCredentials consumerCredentials = new ConsumerCredentials(CERTIFICATE_COMMON_NAME, consumerSecret);
        final Feature filterFeature = OAuth1ClientSupport
                .builder(consumerCredentials)
                .signatureMethod(SIGNATURE_METHOD)
                .feature()
                .build();

        client.register(filterFeature);
        return client;
    }
}
