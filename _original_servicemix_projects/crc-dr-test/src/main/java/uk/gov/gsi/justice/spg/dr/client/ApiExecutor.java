package uk.gov.gsi.justice.spg.dr.client;

import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import uk.gov.gsi.justice.spg.dr.client.validators.*;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.logging.Logger;

class ApiExecutor {



    private static Logger log = null;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        log = Logger.getLogger(ApiExecutor.class.getName());
    }

    private final Client client;
    private final URI uri;


    private String[] args;
    private int counter;
    private WebTarget webTarget;
    private MultivaluedMap<String, Object> headers;
    private API api;

    public ApiExecutor(String[] args, int counter, URI uri, MultivaluedMap<String, Object> headers, API api, String trustStoreFile, String trustStorePassword, String keyStoreFile, String keyPassword, String alias) {
        this.args = args;

        this.counter = counter;

        this.client = getClient(trustStoreFile, trustStorePassword, keyStoreFile, keyPassword, alias);

        this.uri = uri;
        this.headers = headers;
        this.api = api;




    }

    private static File findFile(String pathname) {
        File file = new File(pathname);
        if (file.exists()) {
            return file;
        }
        log.info("File does not exist: " + pathname);
        return null;
    }

    private static boolean isValidCommand(int actualArgs, int expectedArgs, String helpMessage) {
        if (actualArgs != expectedArgs) {
            log.info(helpMessage);
            return false;
        }
        return true;
    }

    private static void printResponseInfoAndSaveFile(Response response, int counter) {


        if (null != response) {
            log.info("HTTP status code : " + response.getStatus());
            MediaType mediaType = response.getMediaType();
            log.info("Media Type : " + mediaType.toString());
            log.info("Response recieved....count <" + counter + ">");


            InputStream entity = response.readEntity(InputStream.class);
            log.info("******************************** OUTPUT FROM SERVER (" + counter + ") ***********************");
            log.info("Output from Server ....count <" + counter + ">");
            Main.saveFile(entity, "udtest", counter);
        }
    }

    public void invoke() {
        String operation;
        Response response=null;
        operation = args[0].toLowerCase();

        synchronized(this) {
            this.webTarget = client.target(this.uri);
        }

        switch (operation) {


            case "search":
                if (SearchCommandValidator.isValidSearchCommand(args)) {
                    response = api.doSearch(webTarget, headers, args);
                    Main.printResponseInfo(response);
                }
                break;
            case "uploadnew":
                if (UploadNewValidator.isValidUploadNewCommand(args)) {
                    System.out.println(webTarget);
                    File file = findFile(args[2]);
                    response = file == null ? null : api.doUploadNew(webTarget, headers, file, args);
                    Main.printResponseInfoAndSaveDocId(response);
                }
                break;
            case "uploadandrelease":
                if (UploadAndReleaseValidator.isValidUploadAndRelease(args)) {
                    File file = findFile(args[2]);
                    response = file == null ? null : api.doUploadAndRelease(webTarget, headers, file, args);
                    Main.printResponseInfoAndSaveDocId(response);
                }

                break;
            case "movedocument":
                if (isValidCommand(args.length, 3, "Please enter valid command. Usage: " + operation + " <DOC_ID> <CRN_ID>")) {
                    response = api.doMoveDocument(webTarget, headers, args[1], args[2]);
                    Main.printResponseInfo(response);
                }
                break;
            case "updatemetadata":
                if (UpdateMetadataValidator.isValidUpdateMetadataCommand(args)) {
                    response = api.doUpdateMetadata(webTarget, headers, args);
                    Main.printResponseInfo(response);
                }
                break;
            //Generic POST, GET, PUT , DELETE REST calls
            case "release":
            case "lock":
                if (isValidCommand(args.length, 2, "Please enter valid command. Usage: " + operation + " <DOC_ID>")) {
                    response = api.doGenericPut(webTarget, headers, args[0], args[1]);
                    Main.printResponseInfo(response);
                }
                break;
            case "undelete":
            case "reserve":
            case "fetchandreserve":
                if (isValidCommand(args.length, 2, "Please enter valid command. Usage: " + operation + " <DOC_ID>")) {
                    log.info("prerequest count: " + counter);
                    response = api.doGenericPost(webTarget, headers, args[0], args[1]);
                    printResponseInfoAndSaveFile(response, counter);
                }
                break;
            case "permissions":
            case "details":
                if (isValidCommand(args.length, 2, "Please enter valid command. Usage: " + operation + " path")) {

                    response = api.doGenericGet(webTarget, headers, args[0], args[1]);
                    Main.printResponseInfo(response);
                }
                break;
            case "fetch":
            case "fetchstream":
                if (isValidCommand(args.length, 2, "Please enter valid command. Usage: " + operation + " path")) {
                    log.info("prerequest count: " + counter);
                    response = api.doGenericGet(webTarget, headers, args[0], args[1]);
                    printResponseInfoAndSaveFile(response, counter);
                }
                break;
            case "sleep":
                if (isValidCommand(args.length, 2, "Please enter valid command. Usage: " + operation + " delayinsecs")) {
                    int delayinSecs = Integer.valueOf(args[1]);

                    response = api.doGenericGet(webTarget, headers, args[0], String.valueOf(delayinSecs * 1000));
                    Main.printResponseInfo(response);
                }
                break;
            case "delete":
            case "deleteall":
            case "deletehard":
            case "deleteallhard":
                String extraParams = operation.equals("deleteall") || operation.equals("deleteallhard") ? " <CRN>" : " <DOC_ID>";
                if (isValidCommand(args.length, 2, "Please enter valid command. Usage: " + operation + extraParams)) {
                    response = api.doGenericDelete(webTarget, headers, args[0], args[1]);
                    Main.printResponseInfo(response);
                }
                break;
            case "multidelete":
                if (MultiDeleteValidator.isValidMultitedeleteCommand(args)) {
                    response = api.doMultiDelete(webTarget, headers, args);
                    Main.printResponseInfo(response);
                }
                break;
            case "notificationstatus":
                response = api.doNotificationStatusGet(webTarget, headers);
                Main.printResponseInfo(response);
                break;
            default:
                log.info("Unrecognised argument " + args[0]);
                break;
        }

        if(response!=null)
            response.close();
    }


    public synchronized Client getClient(String trustStoreFile, String trustStorePassword, String keyStoreFile, String keyPassword, String alias)  {


        SslConfigurator sslConfig = SslConfigurator.newInstance().trustStoreFile(trustStoreFile)
                .trustStorePassword(trustStorePassword).keyStoreFile(keyStoreFile).keyPassword(keyPassword);

        SSLContext sslContext = sslConfig.createSSLContext();
        Client client = ClientBuilder.newBuilder().sslContext(sslContext).build();

        String consumerSecret = null;
        try {
            consumerSecret = getPrivateKeyAsStringFromKeyStore(keyStoreFile, keyPassword, alias);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        ConsumerCredentials consumerCredentials = new ConsumerCredentials(alias, consumerSecret);
        Feature filterFeature = OAuth1ClientSupport
                .builder(consumerCredentials)
                .signatureMethod("RSA-SHA1")
                .feature()
                .build();

        client.register(filterFeature);
        return client;
    }

    private synchronized String getPrivateKeyAsStringFromKeyStore(String keyStoreFile, String password, String alias)
            throws NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException,
            KeyStoreException {

        FileInputStream is = new FileInputStream(keyStoreFile);

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, password.toCharArray());

        Key key = keystore.getKey(alias, password.toCharArray());

        return DatatypeConverter.printBase64Binary(key.getEncoded());

    }



}