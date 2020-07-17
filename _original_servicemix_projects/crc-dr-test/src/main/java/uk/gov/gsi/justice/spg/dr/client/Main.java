package uk.gov.gsi.justice.spg.dr.client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import javax.ws.rs.core.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

public class Main {


    private static Logger log = null;
    private static List<String> uploadedIds = new ArrayList();

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        log = Logger.getLogger(Main.class.getName());
    }

    //Used for log info
    private static String serviceAccountUsername;
    private static String realUsername;
    private static String host;
    private static String port;

    private static int downloadCount = 0;//null;//new Integer();

    private static final String SAVETO = "/tmp" + File.separator + "udclientsavefiles" + File.separator;
    private static String UPLOADED_IDS_PATH = SAVETO + "uploaded-ids.serialized";

    static {

        if (!Files.exists(Paths.get(SAVETO))) {
            try {
                log.info("Creating save directory " + SAVETO);
                Files.createDirectory(Paths.get(SAVETO));
            } catch (IOException e) {
                log.info("Unable to create save directory :" + e);
            }
        }

        if (Files.exists(Paths.get(UPLOADED_IDS_PATH))) {
            log.info("Loading fetched ids");
            loadIDs();
        }
        else
        {
            log.info("No file to load fetched ids");
        }

    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException,
            UnrecoverableKeyException, CertificateException, KeyStoreException {


        Properties props = new Properties();

        // Get the name of the properties file from the command line if
        // specified - otherwise assume etc/spg.dr.client.cfg
        String propertiesFile = "/opt/spg/servicemix/etc/spg.dr.client.cfg";

        for (int i = 0; i < args.length; i++) {
            if ("-properties".equals(args[i]) && args.length > (i + 1)) {
                propertiesFile = args[i + 1];
            }
        }

        log.info("Using properties configuration file <" + propertiesFile + ">");
        try (FileInputStream in = new FileInputStream(propertiesFile)) {
            props.load(in);
        } catch (IOException e) {
            log.severe("Unable to read properties file : " + e);
        }
        String scheme = props.getProperty("spg.dr.scheme");
        String path = props.getProperty("spg.dr.endpoint");
        String trustStoreFile = props.getProperty("spg.dr.truststore");
        String trustStorePassword = props.getProperty("spg.dr.truststore.password");
        String keyStoreFile = props.getProperty("spg.dr.keystore");
        String keyPassword = props.getProperty("spg.dr.keystore.password");
        String alias = props.getProperty("spg.dr.keystore.privateKey.alias");
        serviceAccountUsername = props.getProperty("spg.dr.serviceaccount.username");
        realUsername = props.getProperty("spg.dr.real.username");
        host = props.getProperty("spg.dr.host");
        port = props.getProperty("spg.dr.port");

        URI uri = null;
        try {
            uri = new URI(scheme, null, host, Integer.parseInt(port), path, null, null);
        } catch (NumberFormatException | URISyntaxException e) {
            log.severe(e.getMessage());
            System.exit(1);
        }

        log.info("URI:" + uri);


        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("X-DocRepository-Remote-User", serviceAccountUsername);
        headers.add("X-DocRepository-Real-Remote-User", realUsername);

        API api = new API(log);


        log.info("******************************* REQUEST HEADERS **************************");
        log.info("X-DocRepository-Real-Remote-User = " + realUsername);
        log.info("X-DocRepository-Remote-User = " + serviceAccountUsername);
        log.info("******************************* HOST INFO ********************************");
        log.info("Host : " + host);
        log.info("Port : " + port);


        if (args.length > 0) {


            int numofthreads = 1;
            boolean shouldUseExistingDocuments = false;


            if (args[0].equalsIgnoreCase("threadtest")) {


                int numberOfArgsLessThreadVars = args.length - 2;
                String[] remainingArgs = new String[numberOfArgsLessThreadVars];
                System.arraycopy(args, 2, remainingArgs, 0, numberOfArgsLessThreadVars);
                numofthreads = Integer.valueOf(args[1]);
                args = remainingArgs;

                shouldUseExistingDocuments = args[0].substring(0, 5).equals("fetch");
                if (shouldUseExistingDocuments) {
                    log.info("Using fetched docs serialized list");


                }

            }

            for (int i = 1; i <= numofthreads; i++) {

                Integer finalI = i;
                String finalArgs[] = Arrays.copyOf(args,args.length);
                URI finalUri = uri;

                if (shouldUseExistingDocuments && uploadedIds.size() >0) {
                    int listSize = uploadedIds.size();
                    int imod = i % (listSize - 1);
                    log.info("replacing arg1 (" + finalArgs[1] + ") with index number " + imod+" of size "+listSize);
                    String fetchID = uploadedIds.get(imod);
                    log.info("Fetch ID "+fetchID);
                    finalArgs[1]=fetchID;
                }


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new ApiExecutor(finalArgs, finalI, finalUri, headers, api, trustStoreFile, trustStorePassword, keyStoreFile, keyPassword, alias).invoke();
                    }
                }).start();


            }


        }


    }


    public static void printResponseInfo(Response response) {


        if (null != response) {
            log.info("HTTP status code : " + response.getStatus());
            log.info("******************************* REQUEST HEADERS **************************");
            log.info("X-DocRepository-Real-Remote-User = " + realUsername);
            log.info("X-DocRepository-Remote-User = " + serviceAccountUsername);
            log.info("******************************* HOST INFO ********************************");
            log.info("Host : " + host);
            log.info("Port : " + port);
            MediaType mediaType = response.getMediaType();
            log.info("Media Type : " + mediaType.toString());


            String entity = response.readEntity(String.class);
            log.info("******************************** OUTPUT FROM SERVER ***********************");
            log.info("Output from Server .... <" + entity + ">");
        }
    }


    public static void printResponseInfoAndSaveDocId(Response response) {


        if (null != response) {
            log.info("HTTP status code : " + response.getStatus());
            log.info("******************************* REQUEST HEADERS **************************");
            log.info("X-DocRepository-Real-Remote-User = " + realUsername);
            log.info("X-DocRepository-Remote-User = " + serviceAccountUsername);
            log.info("******************************* HOST INFO ********************************");
            log.info("Host : " + host);
            log.info("Port : " + port);
            MediaType mediaType = response.getMediaType();
            log.info("Media Type : " + mediaType.toString());


            String entity = response.readEntity(String.class);
            log.info("******************************** OUTPUT FROM SERVER ***********************");
            log.info("Output from Server .... \n" + entity);


            int cheapIdParserStartFieldName = entity.indexOf("\"ID\":");
            if (cheapIdParserStartFieldName>0) {
                int cheapIdParserStartFieldValue = cheapIdParserStartFieldName + 6;
                int cheapIdParserEndFieldValue = entity.indexOf("\"", cheapIdParserStartFieldName + 6);

                String cheapIdValue = entity.substring(cheapIdParserStartFieldValue, cheapIdParserEndFieldValue);

                if (cheapIdValue != null && cheapIdValue.length() > 8) {//>8 to skip any "%20%20" responses
                    log.info("extracted ID value: " + cheapIdValue);

                    uploadedIds.add(cheapIdValue);

                    saveIDs();
                } else {
                    log.info("No valid id to extract");

                }
            }

        }
    }


    public synchronized static void saveIDs() {
        ObjectOutputStream oos = null;
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(UPLOADED_IDS_PATH);
            oos = new ObjectOutputStream(fout);

            oos.writeObject(uploadedIds);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (oos != null)
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


        }
    }

    public static void loadIDs() {
        log.info("loading doc ids");
        ObjectInputStream objectinputstream = null;
        try {
            FileInputStream streamIn = new FileInputStream(UPLOADED_IDS_PATH);
            objectinputstream = new ObjectInputStream(streamIn);
            uploadedIds = (List<String>) objectinputstream.readObject();
            for (String item : uploadedIds) {
                log.info("initialising item ... " + item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectinputstream != null) {
                try {
                    objectinputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void saveFile(InputStream is, String fileName, int counter) {

        Date date = Calendar.getInstance().getTime();
        java.nio.file.Path path = Paths.get(SAVETO + File.separator + date.getTime() + "____" + counter + "_" + fileName);

        try {
            Files.copy(is, path);
        } catch (IOException e) {
            log.info("Unable to save file " + e);
        }
        downloadCount++;//.getAndIncrement();
        log.info("File writing finished....number <" + counter + "> total "+downloadCount/*.toString()*/);

    }


}