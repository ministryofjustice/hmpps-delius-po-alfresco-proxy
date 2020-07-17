package uk.gov.gsi.justice.spg.dr.client;

import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.xml.bind.DatatypeConverter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;

public class WebTargetProvider {
    private final String scheme;
    private final String path;
    private final String trustStoreFile;
    private final String trustStorePassword;
    private final String keyStoreFile;
    private final String keyPassword;
    private final String alias;
    private final String host;
    private final String port;

    public WebTargetProvider(String scheme,
                             String path,
                             String trustStoreFile,
                             String trustStorePassword,
                             String keyStoreFile,
                             String keyPassword,
                             String alias,
                             String host,
                             String port) {
        this.scheme = scheme;
        this.path = path;
        this.trustStoreFile = trustStoreFile;
        this.trustStorePassword = trustStorePassword;
        this.keyStoreFile = keyStoreFile;
        this.keyPassword = keyPassword;
        this.alias = alias;
        this.host = host;
        this.port = port;
    }

    public synchronized WebTarget provideWebTarget() {
        final Client client = buildClient();
        final URI uri = buildURI();

        return client.target(uri);
    }

    private synchronized URI buildURI() {
        URI uri = null;
        try {
            uri = new URI(scheme, null, host, Integer.parseInt(port), path, null, null);
        } catch (NumberFormatException | URISyntaxException e) {
            e.printStackTrace();
        }

        return uri;
    }

    private synchronized Client buildClient()  {
        final SslConfigurator sslConfig = SslConfigurator.newInstance().trustStoreFile(trustStoreFile)
                .trustStorePassword(trustStorePassword).keyStoreFile(keyStoreFile).keyPassword(keyPassword);
        final SSLContext sslContext = sslConfig.createSSLContext();
        final Client client = ClientBuilder.newBuilder().sslContext(sslContext).build();

        String consumerSecret = null;
        try {
            consumerSecret = getPrivateKeyAsStringFromKeyStore(keyStoreFile, keyPassword, alias);
        } catch (NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException | KeyStoreException e) {
            e.printStackTrace();
        }

        final ConsumerCredentials consumerCredentials = new ConsumerCredentials(alias, consumerSecret);
        final Feature filterFeature = OAuth1ClientSupport
                .builder(consumerCredentials)
                .signatureMethod("RSA-SHA1")
                .feature()
                .build();

        client.register(filterFeature);
        return client;
    }

    private synchronized String getPrivateKeyAsStringFromKeyStore(String keyStoreFile, String password, String alias)
            throws NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyStoreException {
        final FileInputStream is = new FileInputStream(keyStoreFile);

        final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, password.toCharArray());

        final Key key = keystore.getKey(alias, password.toCharArray());

        return DatatypeConverter.printBase64Binary(key.getEncoded());
    }
}
