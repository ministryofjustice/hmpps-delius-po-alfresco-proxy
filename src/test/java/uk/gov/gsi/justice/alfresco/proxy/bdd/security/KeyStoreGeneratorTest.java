package uk.gov.gsi.justice.alfresco.proxy.bdd.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.gov.gsi.justice.alfresco.proxy.bdd.util.KeyStoreConfigs.ALIAS;
import static uk.gov.gsi.justice.alfresco.proxy.bdd.util.KeyStoreConfigs.PASSWORD;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import org.junit.Test;

public class KeyStoreGeneratorTest {

  @Test
  public void shouldGenerateKeyStoreCertificate() throws Exception {
    final KeyStore keyStore = new KeyStoreGenerator().getKeyStore();

    final X509Certificate cert = (X509Certificate) keyStore.getCertificate("localhost");

    assertEquals("X.509", cert.getType());
    assertEquals("CN=localhost", cert.getSubjectDN().getName());
    assertEquals(cert.getSubjectDN(), cert.getIssuerDN());
    assertEquals("SHA1withRSA", cert.getSigAlgName());
    assertEquals(3, cert.getVersion());
  }

  @Test
  public void shouldGenerateKeyStorePrivateKey() throws Exception {
    final KeyStore keyStore = new KeyStoreGenerator().getKeyStore();

    final PrivateKey privateKey = (PrivateKey) keyStore.getKey(ALIAS, PASSWORD.toCharArray());

    assertNotNull(privateKey);
    assertEquals("RSA", privateKey.getAlgorithm());
    assertEquals("PKCS#8", privateKey.getFormat());
  }
}
