package uk.gov.gsi.justice.po.alfresco.proxy.service;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import uk.gov.gsi.justice.po.alfresco.proxy.http.CxfHttpInterface;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class CxfServerInterface implements CxfHttpInterface {
    private final CxfClientInterface client;

    @Inject
    public CxfServerInterface(CxfClientInterface client) {
        this.client = client;
    }

    @Override
    public Response details(String documentId) {
        return client.details(documentId);
    }

    @Override
    public Response uploadnew(MultipartBody body) {
        return client.uploadnew(body);
    }
}
