package uk.gov.gsi.justice.po.alfresco.proxy.spg;


import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;
import java.net.URI;

/**
 * For adding query params from the message headers to the uri (setting HttpClientApi=false on the unstructured cxf rs server
 * seems to not append query params to the uri).
 */
public class ProxyUDClientRequestFilter implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext clientRequestContext) throws IOException {
        String queryParams = clientRequestContext.getHeaderString("org.apache.cxf.message.Message.QUERY_STRING");
        if (queryParams != null) {
            clientRequestContext.setUri(URI.create(clientRequestContext.getUri().toASCIIString() + "?" + queryParams));
        }
    }
}
