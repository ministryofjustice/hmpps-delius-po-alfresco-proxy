package uk.gov.gsi.justice.alfresco.proxy.bdd.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.operator.OperatorCreationException;
import org.junit.Test;

public class SelfSignedCertificateGeneratorTest {

  @Test
  public void createSelfSignedCertificate()
      throws CertificateException, CertIOException, OperatorCreationException,
          NoSuchAlgorithmException {
    final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    keyPairGenerator.initialize(4096);
    final KeyPair keyPair = keyPairGenerator.generateKeyPair();
    final X509Certificate cert =
        SelfSignedCertificateGenerator.generate(keyPair, "SHA1withRSA", "localhost", 730);

    assertNull(cert.getKeyUsage());
    assertNull(cert.getExtendedKeyUsage());
    assertEquals("X.509", cert.getType());
    assertEquals("CN=localhost", cert.getSubjectDN().getName());
    assertEquals(cert.getSubjectDN(), cert.getIssuerDN());
    assertEquals("SHA1withRSA", cert.getSigAlgName());
    assertEquals(3, cert.getVersion());
  }
}
