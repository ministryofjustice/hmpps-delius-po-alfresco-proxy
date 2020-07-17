package uk.gov.gsi.justice.spg.dr.client;

import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.gsi.justice.spg.stub.json.UploadnewResponse;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class APITest {

    private static Logger log = Logger.getLogger(APITest.class.getName());

	static API api;
	static WebTarget serviceTarget;
	static Client client;
	static Client clientWithoutOAuthFilter;
	static URI uri;
	static MultivaluedMap<String, Object> headers;

	static String consumerSecret;

	static String keyStoreFile;
	static String keyPassword;
	static String alias;

	@BeforeClass
	public static void setUpBeforeClass() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
		api = new API(log);

		Properties props = new Properties();
		try (FileInputStream in = new FileInputStream("spg.dr.client.cfg")) {
			props.load(in);
		} catch (IOException e) {
			log.severe("Unable to read spg.dr.client.cfg properties file, " + e);
		}

		String scheme = props.getProperty("spg.dr.scheme");
		String host = props.getProperty("spg.dr.host");
		String port = props.getProperty("spg.dr.port");
		String path = props.getProperty("spg.dr.endpoint");
		String serviceaccountUsername = props.getProperty("spg.dr.serviceaccount.username");
		String realUsername = props.getProperty("spg.dr.real.username");
		String trustStoreFile = "src/test/resources/" + props.getProperty("spg.dr.truststore");
		String trustStorePassword = props.getProperty("spg.dr.truststore.password");
	    keyStoreFile = "src/test/resources/" + props.getProperty("spg.dr.keystore");
		keyPassword = props.getProperty("spg.dr.keystore.password");
		alias =  props.getProperty("spg.dr.keystore.privateKey.alias");

		consumerSecret = getPrivateKeyAsStringFromKeyStore(keyStoreFile, keyPassword, alias);

		try {
			uri = new URI(scheme, null, host, Integer.parseInt(port), path, null, null);
		} catch (NumberFormatException | URISyntaxException e) {
			log.severe(e.getMessage());
			fail(e.getMessage());
		}

		SslConfigurator sslConfig = SslConfigurator.newInstance().trustStoreFile(trustStoreFile)
				.trustStorePassword(trustStorePassword).keyStoreFile(keyStoreFile).keyPassword(keyPassword);

		SSLContext sslContext = sslConfig.createSSLContext();
		client = ClientBuilder.newBuilder().sslContext(sslContext).build();
		clientWithoutOAuthFilter = ClientBuilder.newBuilder().sslContext(sslContext).build();

		headers = new MultivaluedHashMap<>();
		headers.add("X-DocRepository-Remote-User", serviceaccountUsername);
		headers.add("X-DocRepository-Real-Remote-User", realUsername);

		ConsumerCredentials consumerCredentials = new ConsumerCredentials(alias, consumerSecret);
		Feature filterFeature = OAuth1ClientSupport.builder(consumerCredentials).signatureMethod("RSA-SHA1").feature()
				.build();

		client.register(filterFeature);

	}

	@AfterClass
	public static void tearDownAfterClass() {
		client.close();
	}

	@Before
	public  void setUp() {
		serviceTarget = client.target(uri);
	}

    @Test
    public void testDoSearch() {
        String[] crn = {"search", "0000000000"};
        Response response = api.doSearch(serviceTarget, headers, crn);
        String json = response.readEntity(String.class);
        log.info("Status... <" + response.getStatus() + ">");
        log.info("Output from Server... <" + json + ">");
        assertEquals(200, response.getStatus());
        response.close();
    }

    @Test
    public void testDoUploadNew() {
        File file = new File("src/test/resources/Lorem_ipsum.txt");
        String[] args = {"uploadnew", "X030927", "test.doc","author=test", "entityType=APREFERRAL","entityId=1","docType=1", "locked=true"};
        Response response = api.doUploadNew(serviceTarget, headers,file,args);
        String json = response.readEntity(String.class);
        log.info("Status... <" + response.getStatus() + ">");
        log.info("Output from Server... <" + json + ">");
        assertEquals(200, response.getStatus());
        response.close();
    }

    @Test
    public void testDoFetch() {
        File file = new File("src/test/resources/Lorem_ipsum.txt");
        String[] args = {"uploadnew", "X030927", "test.doc","author=test", "entityType=APREFERRAL","entityId=1","docType=1"};
        Response response = api.doUploadNew(serviceTarget, headers, file, args);
        UploadnewResponse uploadnewResponse = response.readEntity(UploadnewResponse.class);
        String docId = uploadnewResponse.getId();
        response = api.doGenericGet(serviceTarget, headers, "fetch", docId);
        String json = response.readEntity(String.class);
        log.info("Status... <" + response.getStatus() + ">");
        log.info("Output from Server... <" + json + ">");
        assertEquals(200, response.getStatus());
        response.close();
    }



    @Test
    public void testDoFetchViaMain() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyStoreException, InvalidKeySpecException {
        File file = new File("src/test/resources/Lorem_ipsum.txt");
        String[] args = {"fetch", "test.doc"};

        Main.main(args);

//        Response response = api.doUploadNew(serviceTarget, headers, file, args);
//        UploadnewResponse uploadnewResponse = response.readEntity(UploadnewResponse.class);
//        String docId = uploadnewResponse.getId();
//        response = api.doGenericGet(serviceTarget, headers, "fetch", docId);
//        String json = response.readEntity(String.class);
//        log.info("Status... <" + response.getStatus() + ">");
//        log.info("Output from Server... <" + json + ">");
//        assertEquals(200, response.getStatus());
//        response.close();
    }



	@Test
	public void testDoFetchAndReserve() {
        File file = new File("src/test/resources/Lorem_ipsum.txt");
        String[] args = {"uploadnew", "X030927", "test.doc","author=test", "entityType=APREFERRAL","entityId=1","docType=1"};
		Response response = api.doUploadNew(serviceTarget, headers,file, args);
		UploadnewResponse uploadnewResponse = response.readEntity(UploadnewResponse.class);
		String docId = uploadnewResponse.getId();
		response = api.doGenericPost(serviceTarget, headers,"fetchandreserve", docId);
		String json = response.readEntity(String.class);
		log.info("Status... <" + response.getStatus() + ">");
		log.info("Output from Server... <" + json + ">");
		assertEquals(200, response.getStatus());
		response.close();
	}

    @Test
    public void testDoUploadAndRelease() {
        File file = new File("src/test/resources/Lorem_ipsum.txt");
        String[] args = {"uploadandrelease", "testDocId", "test.doc", "author=test" ,"entityType=APREFFERAL"};
        Response response = api.doUploadAndRelease(serviceTarget, headers,  file, args);
        String json = response.readEntity(String.class);
        log.info("Status... <" + response.getStatus() + ">");
        log.info("Output from Server... <" + json + ">");
        assertEquals(200, response.getStatus());
        response.close();
    }

    @Test
    public void testDoDelete() {
        String docId = "6666666666";
        String [] services = {"delete", "deleteall","deletehard", "deleteallhard"};
        for (int i  = 0 ; i < services.length ; i++){
            Response response = api.doGenericDelete(serviceTarget, headers,services[i], docId);
            String json = response.readEntity(String.class);
            log.info("Status... <" + response.getStatus() + ">");
            log.info("Output from Server... <" + json + ">");
            assertEquals(200, response.getStatus());
            response.close();
        }
    }

    @Test
    public void testDoGenericPut() {
        String docId = "7777777777";
        String [] serviceNames = {"release","lock"};
        for (int i = 0; i < serviceNames.length; i++) {
            Response response = api.doGenericPut(serviceTarget, headers,serviceNames[i], docId);
            String json = response.readEntity(String.class);
            log.info("Status... <" + response.getStatus() + ">");
            log.info("Output from Server... <" + json + ">");
            assertEquals(200, response.getStatus());
            response.close();
        }
    }

    @Test
    public void testMoveDocument() {
        String docId = "8888888888";
        String crn = "123";
        Response response = api.doMoveDocument(serviceTarget, headers, docId, crn);
        String json = response.readEntity(String.class);
        log.info("Status... <" + response.getStatus() + ">");
        log.info("Output from Server... <" + json + ">");
        assertEquals(200, response.getStatus());
        response.close();
    }

    @Test
    public void testUpdateMetadata() {
        String[] crn = {"updatemetadata", "0000000000", "fileName=hello" ,"author=Tester"};
        Response response = api.doUpdateMetadata(serviceTarget, headers, crn);
        String json = response.readEntity(String.class);
        log.info("Status... <" + response.getStatus() + ">");
        log.info("Output from Server... <" + json + ">");
        assertEquals(200, response.getStatus());
        response.close();
    }

    @Test
    public void testGenericGet() {
        String crn = "TRT";
        String[] serviceName = {"details", "permissions"};
        for (int i = 0; i < serviceName.length; i++) {
            Response response = api.doGenericGet(serviceTarget, headers, serviceName[i], crn);
            String json = response.readEntity(String.class);
            log.info("Status... <" + response.getStatus() + ">");
            log.info("Output from Server... <" + json + ">");
            assertEquals(200, response.getStatus());
            response.close();
        }

    }

    @Test
    public void testGenericPost() {
        String crn = "0000000000";
        String[] serviceName = {"undelete", "reserve"};
        for (int i = 0; i < serviceName.length; i++) {
            Response response = api.doGenericPost(serviceTarget, headers, serviceName[i], crn);
            String json = response.readEntity(String.class);
            log.info("Status... <" + response.getStatus() + ">");
            log.info("Output from Server... <" + json + ">");
            assertEquals(200, response.getStatus());
            response.close();
        }
    }

    @Test
    public void testWithoutOauthSignature() {
        serviceTarget = clientWithoutOAuthFilter.target(uri);

        String[] crn = {"search", "0000000000"};
        Response response = api.doSearch(serviceTarget, headers, crn);
        String json = response.readEntity(String.class);
        log.info("Status... <" + response.getStatus() + ">");
        log.info("Output from Server... <" + json + ">");
        assertEquals(401, response.getStatus());
        response.close();
    }

    @Test
    public void testNotificationStatus() {
        Response response = api.doNotificationStatusGet(serviceTarget, headers);
        String json = response.readEntity(String.class);
        log.info("Status... <" + response.getStatus() + ">");
        log.info("Output from Server... <" + json + ">");
        assertEquals(200, response.getStatus());
        response.close();
    }

    public static String getPrivateKeyAsStringFromKeyStore(String keyStoreFile, String password, String alias) throws NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyStoreException {

        FileInputStream is = new FileInputStream(keyStoreFile);

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, password.toCharArray());

        Key key = keystore.getKey(alias, password.toCharArray());

        return DatatypeConverter.printBase64Binary(key.getEncoded());

    }
}
