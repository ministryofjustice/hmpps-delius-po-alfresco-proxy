package uk.gov.gsi.justice.po.alfresco.proxy.spg;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.message.Attachment;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

public interface SPGUnstructuredService {

	/*
	 * Basic "ping" -  not part of API, just for testing
	 */
	@GET
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	Response ping();
	
	/*
	 * The Zaizi Webscripts API supports the following generic GET commands
	 * 	search
	 * 	fetch
	 */




	//sleep is not part of the alfresco API. was used in early performance issues diagnostics
	@GET
	@Path("/sleep/{delay}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response threadtest(@PathParam("delay") String delay) ;


	@GET
	@Path("/search/{CRN}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	Response search(@PathParam("CRN") String crn) ;

	@GET
	@Path("/fetch/{doc_id}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	Response fetch(@PathParam("doc_id") String documentId);


	@GET
	@Path("/fetchstream/{doc_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response fetchstream(@PathParam("doc_id") String documentId);



	/*
	 * The Zaizi Webscripts API supports the following generic PUT commands
	 * 	release
	 * 	lock
	 * 
	 * This service endpoint definition will match any PUT requests
	 * that accept a single parameter after the method being invoked
	 * 
	 * e.g. 
	 *   [rootpath]/lock/[document id]
	 *   [rootpath]/release/[document id]
	 */
	@PUT
	@Path("/lock/{doc_id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	Response lock(@PathParam("doc_id") String documentId);

	@PUT
	@Path("/release/{doc_id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	Response release(@PathParam("doc_id") String documentId);
	/*
	 * The Zaizi Webscripts API supports the following generic POST commands
	 * 	fetchandreserve
	 *
	 * This service endpoint definition will match any PUT requests
	 * that accept a single parameter after the method being invoked
	 * 
	 * e.g. 
	 *   [rootpath]/fetchandreserve/[document id]
	 */
	@POST
	@Path("/fetchandreserve/{doc_id}")
	Response fetchandreserve(@PathParam("doc_id") String documentId);


	/* The following endpoints have specific named paths as they do not follow a generic parameter pattern */
	
	/**
	 * This may need to be specified as accepting List<Attachment> instead of individual Multiparts
	 *
	 * @return
	 * @throws Exception
	 *
	 */
	@POST
	@Path("/uploadnew")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
	Response uploadnew(MultipartBody body);

	/**
	 * Used to upload a previously "fetchandreserved" document
	 * Same as uploadnew, except needs the doc_id as a path parameter
	 *  
	 * @param documentId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/uploadandrelease/{doc_id}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
	Response uploadandrelease(@PathParam("doc_id") String documentId, MultipartBody body);

	/**
	 * 
	 * @param documentId
	 * @return
	 * @hrows Exception
	 */
	@DELETE
	@Path("/delete/{doc_id}")
	@Produces(MediaType.APPLICATION_JSON)
	Response delete(@PathParam("doc_id") String documentId);

	/**
	 * 
	 * @param crn
	 * @return
	 * @throws Exception
	 */
	@DELETE
	@Path("/deleteall/{CRN}")
	@Produces(MediaType.APPLICATION_JSON)
	Response deleteall(@PathParam("CRN") String crn);

	@DELETE
	@Path("/deletehard/{doc_id}")
	@Produces(MediaType.APPLICATION_JSON)
	Response deletehard(@PathParam("doc_id") String documentId);

	@DELETE
	@Path("/deleteallhard/{CRN}")
	@Produces(MediaType.APPLICATION_JSON)
	Response deleteallhard(@PathParam("CRN") String crn);

	@POST
	@Path("/multidelete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response multidelete(Map documentIds);

    @POST
    @Path("/movedocument/{doc_id}/{CRN}")
    @Produces(MediaType.APPLICATION_JSON)
    Response movedocument(@PathParam("doc_id") String documentId, @PathParam("CRN") String crn);

	@POST
	@Path("/reserve/{doc_id}")
	@Produces(MediaType.APPLICATION_JSON)
	Response reserve(@PathParam("doc_id") String documentId);

	@POST
	@Path("/undelete/{doc_id}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	Response undelete(@PathParam("doc_id") String documentId);


	@POST
	@Path("/updatemetadata/{doc_id}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	Response updatemetadata(@PathParam("doc_id") String documentId, MultipartBody body);

	@GET
	@Path("/details/{doc_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response details(@PathParam("doc_id") String documentId);

	@GET
	@Path("/permissions/{doc_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response permissions(@PathParam("doc_id") String documentId);

	@GET
	@Path("notificationStatus")
	@Produces(MediaType.APPLICATION_JSON)
	Response notificationStatus();

	/*
	 * AssignOffender is not a CRC exposed api call, so is not required in the SPG Proxy Service
	 */
	
	/* ************************************************************************
	 * Test endpoints for debugging POST problems
	 * ************************************************************************/
	@POST
	@Path("/uploadparam")
	@Produces(MediaType.APPLICATION_JSON)
	Response testpost(
			@PathParam("CRN") String crnId);
	
	@POST
	@Path("/uploadbean")
    @Produces(MediaType.APPLICATION_JSON)
	Response testpost1(
			Object bean);

	@POST
	@Path("/uploadmultipart")
	@Produces(MediaType.APPLICATION_JSON)
	Response testpost2(
			@Multipart(value = "CRN", type = "text/plain") String crnId,
			@Multipart(value = "Author", type = "text/plain") String author);
	
	@POST
	@Path("/uploadmultipartbody")
	@Produces(MediaType.APPLICATION_JSON)
	Response testpost3(
			MultipartBody crnId);
	

	@POST
	@Path("/uploadlist")
	@Produces(MediaType.APPLICATION_JSON)
	Response testpost4(List<Attachment> atts);
	
	@POST
	@Path("/uploadform")
    @Produces(MediaType.APPLICATION_JSON)
	Response testpost5();
    
    @POST
    @Path("/uploadmvm")
    @Produces(MediaType.APPLICATION_JSON)
	Response testpost6(MultivaluedMap<String, String> data);

}
