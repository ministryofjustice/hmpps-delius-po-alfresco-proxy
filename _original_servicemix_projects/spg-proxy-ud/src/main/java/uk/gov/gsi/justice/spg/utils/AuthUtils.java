package uk.gov.gsi.justice.spg.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class AuthUtils {

    public static Certificate getCertificate(String keyStoreFile, String password, String alias)
            throws NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException,
            KeyStoreException {

        FileInputStream is = new FileInputStream(keyStoreFile);

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, password.toCharArray());

        return keystore.getCertificate(alias);
    }

}
