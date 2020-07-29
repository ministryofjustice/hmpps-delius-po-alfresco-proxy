package uk.gov.gsi.justice.po.alfresco.proxy.cxf.server;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.gsi.justice.po.alfresco.proxy.cxf.CxfHttpInterface;
import uk.gov.gsi.justice.po.alfresco.proxy.cxf.client.CxfClientInterface;

import javax.ws.rs.core.Response;

public class CxfServerHandler implements CxfHttpInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(CxfServerHandler.class);
    private final CxfClientInterface client;

    public CxfServerHandler(CxfClientInterface client) {
        this.client = client;
    }

    @Override
    public Response details(String documentId) {
        LOGGER.info("====================> Document ID = {}", documentId);
        final Response response = client.details(documentId);
//        final String body = response.readEntity(String.class);
//
//        LOGGER.info("====================> Response Entity = {}", body);

        return response;
    }

    @Override
    public Response uploadnew(MultipartBody body) {
        return client.uploadnew(body);
    }
}
