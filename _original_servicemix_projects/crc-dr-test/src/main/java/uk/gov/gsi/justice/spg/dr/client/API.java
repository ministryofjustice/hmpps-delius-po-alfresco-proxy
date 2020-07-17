package uk.gov.gsi.justice.spg.dr.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class API {

    Logger log;
    private final static String TARGET = "Target URI: ";

    public API(Logger log) {
        this.log = log;
    }


//    public Response doSleep(WebTarget webTarget, MultivaluedMap<String, Object> headers, String service, String delay) {
//
//        WebTarget threadTestTarget = webTarget.path(service+"/"+delay);
////        searchTarget = addQueryParams(args, searchTarget);
//        log.info(TARGET.concat(threadTestTarget.getUri().toString()));
//            Response response = threadTestTarget.request(MediaType.APPLICATION_JSON_TYPE).headers(headers).get();
//        return response;
//    }


    public Response doSearch(WebTarget webTarget, MultivaluedMap<String, Object> headers, String[] crn) {

        WebTarget searchTarget = webTarget.path(crn[0] + "/" + crn[1]);
        searchTarget = addQueryParams(crn, searchTarget);
        log.info(TARGET.concat(searchTarget.getUri().toString()));
        Response response = searchTarget.request(MediaType.APPLICATION_JSON_TYPE).headers(headers).get();
        return response;
    }

    public Response doUploadNew(WebTarget webTarget, MultivaluedMap<String, Object> headers, File file,  String[] args) {

        FileDataBodyPart filePart = new FileDataBodyPart("filedata", file);
        MultiPart multipart = addMultipartData(args, 3, true).bodyPart(filePart);
        WebTarget uploadTarget = webTarget.register(MultiPartFeature.class).path("uploadnew");
        log.info(TARGET.concat(uploadTarget.getUri().toString()));
        Response response = uploadTarget.request(MediaType.APPLICATION_JSON_TYPE).headers(headers).post(Entity.entity(multipart, multipart.getMediaType()));
        return response;
    }

    public Response doUploadAndRelease(WebTarget webTarget, MultivaluedMap<String, Object> headers, File file, String[] args) {

        FileDataBodyPart filePart = new FileDataBodyPart("filedata", file);
        MultiPart multipart = addMultipartData(args, 3 ,false).bodyPart(filePart);
        WebTarget uploadAndReleaseTarget = webTarget.register(MultiPartFeature.class).path("uploadandrelease/" + args[1]);
        log.info(TARGET.concat(uploadAndReleaseTarget.getUri().toString()));
        Response response = uploadAndReleaseTarget.request(MediaType.APPLICATION_JSON_TYPE).headers(headers).post(Entity.entity(multipart, multipart.getMediaType()));
        return response;
    }

    public Response doGenericPut(WebTarget webTarget, MultivaluedMap<String, Object> headers, String service, String path) {

        WebTarget lockTarget = webTarget.path(service + "/" + path);
        log.info(TARGET.concat(lockTarget.getUri().toString()));
        lockTarget.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);

        Response response = lockTarget.request(MediaType.APPLICATION_JSON_TYPE).headers(headers).put(null);
        return response;
    }

    public Response doMoveDocument(WebTarget webTarget, MultivaluedMap<String, Object> headers, String docId, String crn) {

        WebTarget moveDocumentTarget = webTarget.path("movedocument/" + docId + "/" + crn);
        log.info(TARGET.concat(moveDocumentTarget.getUri().toString()));
        Response response = moveDocumentTarget.request().headers(headers).post(null);
        return response;
    }

    public Response doUpdateMetadata(WebTarget serviceTarget, MultivaluedMap<String, Object> headers, String[] args) {

        WebTarget updateMetadataTarget = serviceTarget.register(MultiPartFeature.class).path(args[0] + "/" + args[1]);
        MultiPart multiPart = addMultipartData(args, 2, false);
        log.info(TARGET.concat(updateMetadataTarget.getUri().toString()));
        Response response = updateMetadataTarget.request(MediaType.APPLICATION_JSON_TYPE).headers(headers).post(Entity.entity(multiPart, multiPart.getMediaType()));
        return response;
    }

    public Response doNotificationStatusGet(WebTarget webTarget, MultivaluedMap<String, Object> headers) {

        WebTarget detailsTarget = webTarget.path("notificationStatus");
        log.info(TARGET.concat(detailsTarget.getUri().toString()));
        Response response = detailsTarget.request().headers(headers).get();
        return response;
    }

    // Combine Permissions, Details and Fetch into one REST call
    public Response doGenericGet(WebTarget webTarget, MultivaluedMap<String, Object> headers, String serviceName, String path) {

        WebTarget detailsTarget = webTarget.path(serviceName + "/" + path);
        log.info(TARGET.concat(detailsTarget.getUri().toString()));
        Response response = detailsTarget.request().headers(headers).get();
        return response;
    }

    //Combine Undelete, Reserve , Declare as record , Fetch and reserve into one REST call
    public Response doGenericPost(WebTarget webTarget, MultivaluedMap<String, Object> headers, String serviceName, String path) {

        WebTarget reserveWebTarget = webTarget.path(serviceName + "/" + path);
        log.info(TARGET.concat(reserveWebTarget.getUri().toString()));
        Response response = reserveWebTarget.request().headers(headers).post(null);
        return response;
    }

    //Combine DeleteAll and Delete REST calls
    public Response doGenericDelete(WebTarget webTarget, MultivaluedMap<String, Object> headers, String serviceName, String path) {

        WebTarget deleteAllTarget = webTarget.path(serviceName + "/" + path);
        log.info(TARGET.concat(deleteAllTarget.getUri().toString()));
        Response response = deleteAllTarget.request(MediaType.APPLICATION_JSON_TYPE).headers(headers).delete();
        return response;
    }

    public Response doMultiDelete(WebTarget webTarget, MultivaluedMap<String, Object> headers, String[] args) {
        WebTarget multiDeleteTarget = webTarget.path(args[0]).register(JacksonJsonProvider.class);
        log.info(TARGET.concat(multiDeleteTarget.getUri().toString()));

        Map documentIdsMap = new HashMap();
        documentIdsMap.put("DOCUMENT_IDS", addDocIds(args));

        Response response = multiDeleteTarget.request(MediaType.APPLICATION_JSON).headers(headers).post(Entity.entity(documentIdsMap, MediaType.APPLICATION_JSON));

        return response;
    }

    private List<String> addDocIds(String[] args) {
        String[] docIdParam = args[1].split("=");
        String[] docIdsValues = docIdParam[1].split(",");

        return Arrays.asList(docIdsValues);
    }

    private WebTarget addQueryParams(String[] crn, WebTarget searchTarget) {
        if (crn.length > 2) {
            for (int i = 2; i < crn.length; i++) {
                String[] queryParams = crn[i].split("=");
                searchTarget = searchTarget.queryParam(queryParams[0].trim(), queryParams[1].trim());
            }
        }
        return searchTarget;
    }

    private MultiPart addMultipartData(String[] crn, int index, boolean isUploadNew) {
        FormDataMultiPart multipart = new FormDataMultiPart();
        if(isUploadNew){
            multipart.field("CRN", crn[1]);
        }
        for (int i = index; i < crn.length; i++) {
            if(crn[i] != null){
                String[] queryParams = crn[i].split("=");
                multipart.field(queryParams[0].trim(), queryParams[1].trim());
            }
        }
        return multipart;
    }

}
