package uk.gov.gsi.justice.alfresco.proxy.bdd.util;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

public class HttpHeadersMapProvider {
    public static MultivaluedMap<String, Object> getMultivaluedMap() {
        final MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("X-DocRepository-Remote-User", "C01");
        headers.add("X-DocRepository-Real-Remote-User", "JaneBloggs");
        return headers;
    }
}
