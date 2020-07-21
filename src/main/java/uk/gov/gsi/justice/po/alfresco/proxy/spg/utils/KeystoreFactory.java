package uk.gov.gsi.justice.po.alfresco.proxy.spg.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.gov.gsi.justice.po.alfresco.proxy.spg.exceptions.InterchangeSenderPermissionDeniedException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class KeystoreFactory {
    private static Log log = LogFactory.getLog(KeystoreFactory.class);

	public static KeyStore getKeystore(String keystoreFilePath, String password) {
		KeyStore keystore = null;

		try {
			keystore = KeyStore.getInstance("JKS");
			InputStream readStream = new FileInputStream(keystoreFilePath);
			keystore.load(readStream, password.toCharArray());
			readStream.close();
		} catch (Exception e) {
			String msg = "Exception when loading Keystore: "+String.valueOf(keystoreFilePath)+"\n"+e.getLocalizedMessage();
			log.error(msg);
			throw new RuntimeException(msg);
		}

		return keystore;
	}

	public static Key getPrivateKey(String keystoreFilePath, String password, String alias) {
		log.info("Getting Keystore: "+String.valueOf(keystoreFilePath)+", alias:"+String.valueOf(alias));
		Key privateKey = null;
		try {
			KeyStore keyStore = getKeystore(keystoreFilePath, password);
			privateKey = getPrivateKey(keyStore, alias, password);

		} catch (Exception e) {
			String msg = "Exception when loading Keystore: "+String.valueOf(keystoreFilePath)+", alias:"+String.valueOf(alias)+"\n"+e.getLocalizedMessage();
			log.error(msg);
			throw new RuntimeException(msg);
		}

		return privateKey;
	}

	private static Key getPrivateKey(KeyStore keyStore, String alias, String password)
			throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
		Key key = keyStore.getKey(alias, password.toCharArray());
		if (key == null) {
			throw new UnrecoverableKeyException("Could not load key with alias: " + alias + " from keystore");
		}
		return key;
	}

	public static PublicKey getPublicKey(String keystoreFilePath, String alias, String password)
			throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
		KeyStore keyStore = getKeystore(keystoreFilePath, password);
		PublicKey publicKey = getPublicKey(keyStore, alias);
		return publicKey;
	}

	private static PublicKey getPublicKey(KeyStore keyStore, String alias) throws KeyStoreException {
		Certificate certificate = keyStore.getCertificate(alias);

		if (certificate == null) {
			throw new KeyStoreException("Could not load certificate with alias: " + alias);
		}

		PublicKey publicKey = certificate.getPublicKey();
		return publicKey;
	}

	public static String getFieldValueFromCertificateSubjectDN(String targetFieldName, X509Certificate certificate) throws InterchangeSenderPermissionDeniedException {
		Principal principal = certificate.getSubjectDN();
		String subjectDN = principal.getName(); // e.g. CN=localhost, OU=CRC, O=MOJ, C=GB

		String[] subjectDNFields = subjectDN.split(",");
		for (int i=0; i < subjectDNFields.length; i++) {
			String[] fieldNameAndValue = subjectDNFields[i].split("="); // e.g. CN=localhost
			String fieldName = fieldNameAndValue[0].trim(); // e.g. CN
			String fieldValue = fieldNameAndValue[1]; // e.g. localhost
			if (fieldName.trim().equals(targetFieldName)) {
				return fieldValue;
			}
		}

		// No Match!
		throw new InterchangeSenderPermissionDeniedException("Could not match certificate alias using subject:"+subjectDN);
	}

}
