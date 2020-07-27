package uk.gov.gsi.justice.po.alfresco.proxy.http;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface CxfHttpInterface {
    @GET
    @Path("/details/{doc_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response details(@PathParam("doc_id") String documentId);

    @POST
    @Path("/uploadnew")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    Response uploadnew(MultipartBody body);
}
