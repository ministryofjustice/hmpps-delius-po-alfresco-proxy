package uk.gov.gsi.justice.po.alfresco.proxy.service;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.gsi.justice.po.alfresco.proxy.http.CxfClientInterface;
import uk.gov.gsi.justice.po.alfresco.proxy.http.CxfHttpInterface;

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
        System.out.println();
        return client.details(documentId);
    }

    @Override
    public Response uploadnew(MultipartBody body) {
        return client.uploadnew(body);
    }
}
