package uk.gov.gsi.justice.spg.dr.client;

import uk.gov.gsi.justice.spg.dr.client.validators.*;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static uk.gov.gsi.justice.spg.dr.client.Main.loadIDs;

public class SpgAlfrescoClient {
    private static Logger LOGGER;

    private final WebTarget webTarget;
    private final MultivaluedMap<String, Object> headers;
    private final API api;

    private static final String SAVETO = "/tmp" + File.separator + "udclientsavefiles" + File.separator;
    private static String UPLOADED_IDS_PATH = SAVETO + "uploaded-ids.serialized";

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        LOGGER = Logger.getLogger(SpgAlfrescoClient.class.getName());

        if (!Files.exists(Paths.get(SAVETO))) {
            try {
                LOGGER.info("Creating save directory " + SAVETO);
                Files.createDirectory(Paths.get(SAVETO));
            } catch (IOException e) {
                LOGGER.info("Unable to create save directory :" + e);
            }
        }

        if (Files.exists(Paths.get(UPLOADED_IDS_PATH))) {
            LOGGER.info("Loading fetched ids");
            loadIDs();
        } else {
            LOGGER.info("No file to load fetched ids");
        }

    }

    public SpgAlfrescoClient(WebTarget webTarget, String serviceAccountUsername, String realUsername) {
        this.webTarget = webTarget;
        this.headers = buildHeaders(serviceAccountUsername, realUsername);

        this.api = new API(LOGGER);
    }

    public Response search(String[] terms) {
        if (!SearchCommandValidator.isValidSearchCommand(terms)) {
            LOGGER.info("Invalid arguments: " + terms);
        }

        return api.doSearch(webTarget, headers, terms);
    }

    public Response uploadnew(String[] terms) {
        if (!UploadNewValidator.isValidUploadNewCommand(terms)) {
            LOGGER.info("Invalid arguments: " + terms);
        }

        File file = findFile(terms[2]);
        return file == null ? null : api.doUploadNew(webTarget, headers, file, terms);
    }

    public Response uploadandrelease(String[] terms) {
        if (UploadAndReleaseValidator.isValidUploadAndRelease(terms)) {
            LOGGER.info("Invalid arguments: " + terms);
        }

        File file = findFile(terms[2]);
        return file == null ? null : api.doUploadAndRelease(webTarget, headers, file, terms);
    }

    public Response movedocument(String[] terms) {
        final String operation = terms[0].toLowerCase();
        if (!isValidCommand(terms.length, 3, "Please enter valid command. Usage: " + operation + " <DOC_ID> <CRN_ID>")) {
            LOGGER.info("Invalid arguments: " + terms);
        }
        return api.doMoveDocument(webTarget, headers, terms[1], terms[2]);
    }

    public Response updatemetadata(String[] terms) {
        if (!UpdateMetadataValidator.isValidUpdateMetadataCommand(terms)) {
            LOGGER.info("Invalid arguments: " + terms);
        }

        return api.doUpdateMetadata(webTarget, headers, terms);
    }

    //Generic POST, GET, PUT , DELETE REST calls
    public Response release(String[] terms) {
        return lock(terms);
    }

    public Response lock(String[] terms) {
        final String operation = terms[0].toLowerCase();
        if (!isValidCommand(terms.length, 2, "Please enter valid command. Usage: " + operation + " <DOC_ID>")) {
            LOGGER.info("Invalid arguments: " + terms);
        }
        return api.doGenericPut(webTarget, headers, terms[0], terms[1]);
    }

    public Response undelete(String[] terms) {
        return fetchandreserve(terms);
    }

    public Response reserve(String[] terms) {
        return fetchandreserve(terms);
    }

    public Response fetchandreserve(String[] terms) {
        final String operation = terms[0].toLowerCase();
        if (!isValidCommand(terms.length, 2, "Please enter valid command. Usage: " + operation + " <DOC_ID>")) {
            LOGGER.info("Invalid arguments: " + terms);
        }
        return api.doGenericPost(webTarget, headers, terms[0], terms[1]);
    }

    public Response permissions(String[] terms) {
        return details(terms);
    }

    public Response details(String[] terms) {
        final String operation = terms[0].toLowerCase();
        if (!isValidCommand(terms.length, 2, "Please enter valid command. Usage: " + operation + " path")) {
            LOGGER.info("Invalid arguments: " + terms);
        }

        return api.doGenericGet(webTarget, headers, terms[0], terms[1]);
    }

    public Response fetch(String[] terms) {
        return fetchstream(terms);
    }

    public Response fetchstream(String[] terms) {
        final String operation = terms[0].toLowerCase();
        if (!isValidCommand(terms.length, 2, "Please enter valid command. Usage: " + operation + " path")) {
            LOGGER.info("Invalid arguments: " + terms);
        }

        return api.doGenericGet(webTarget, headers, terms[0], terms[1]);
    }

    public Response sleep(String[] terms) {
        final String operation = terms[0].toLowerCase();
        if (!isValidCommand(terms.length, 2, "Please enter valid command. Usage: " + operation + " delayinsecs")) {
            LOGGER.info("Invalid arguments: " + terms);
        }
        int delayinSecs = Integer.valueOf(terms[1]);
        return api.doGenericGet(webTarget, headers, terms[0], String.valueOf(delayinSecs * 1000));
    }

    public Response delete(String[] terms) {
        return deleteallhard(terms);
    }

    public Response deleteall(String[] terms) {
        return deleteallhard(terms);
    }

    public Response deletehard(String[] terms) {
        return deleteallhard(terms);
    }

    public Response deleteallhard(String[] terms) {
        final String operation = terms[0].toLowerCase();
        String extraParams = operation.equals("deleteall") || operation.equals("deleteallhard") ? " <CRN>" : " <DOC_ID>";
        if (!isValidCommand(terms.length, 2, "Please enter valid command. Usage: " + operation + extraParams)) {
            LOGGER.info("Invalid arguments: " + terms);
        }
        return api.doGenericDelete(webTarget, headers, terms[0], terms[1]);
    }

    public Response multidelete(String[] terms) {
        if (!MultiDeleteValidator.isValidMultitedeleteCommand(terms)) {
            LOGGER.info("Invalid arguments: " + terms);
        }
        return api.doMultiDelete(webTarget, headers, terms);
    }

    public Response notificationstatus(String[] terms) {
        return api.doNotificationStatusGet(webTarget, headers);
    }

    private MultivaluedMap<String, Object> buildHeaders(String serviceAccountUsername, String realUsername) {
        final MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("X-DocRepository-Remote-User", serviceAccountUsername);
        headers.add("X-DocRepository-Real-Remote-User", realUsername);

        return headers;
    }

    private File findFile(String pathname) {
        File file = new File(pathname);
        if (file.exists()) {
            return file;
        }
        LOGGER.info("File does not exist: " + pathname);
        return null;
    }

    private boolean isValidCommand(int actualArgs, int expectedArgs, String helpMessage) {
        if (actualArgs != expectedArgs) {
            LOGGER.info(helpMessage);
            return false;
        }
        return true;
    }
}
