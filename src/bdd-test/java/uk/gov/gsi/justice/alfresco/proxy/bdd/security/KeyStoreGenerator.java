package uk.gov.gsi.justice.alfresco.proxy.bdd.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import static uk.gov.gsi.justice.alfresco.proxy.bdd.util.KeyStoreConfigs.*;

public class KeyStoreGenerator {

    public KeyStore getKeyStore() {
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_SIZE);

            final KeyPair keyPair = keyPairGenerator.generateKeyPair();
            final X509Certificate cert = SelfSignedCertificateGenerator.generate(keyPair, HASH_ALGORITHM, CERTIFICATE_COMMON_NAME, 730);
            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            keyStore.load(null, null);

            keyStore.setCertificateEntry(ALIAS, cert);
            keyStore.setKeyEntry(ALIAS, keyPair.getPrivate(), PASSWORD.toCharArray(), new X509Certificate[]{cert});

            return keyStore;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
