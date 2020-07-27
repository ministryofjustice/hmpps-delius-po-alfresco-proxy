package uk.gov.gsi.justice.po.alfresco.proxy.service;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import uk.gov.gsi.justice.po.alfresco.proxy.http.CxfHttpInterface;

import javax.ws.rs.core.Response;

public class CxfClientInterface implements CxfHttpInterface {
    @Override
    public Response details(String documentId) {
        return null;
    }

    @Override
    public Response uploadnew(MultipartBody body) {
        return null;
    }
}
