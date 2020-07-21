package uk.gov.gsi.justice.po.alfresco.proxy.spg;


import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Provider
public class MapToJsonBodyWriter implements MessageBodyWriter<Map> {

    public long getSize(Map map, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Map.class.isAssignableFrom(type)
                && mediaType.toString().equals(MediaType.APPLICATION_JSON.toString());
    }

    public void writeTo(Map map, Class<?> clazz, Type type, Annotation[] annotations,
                        MediaType mt, MultivaluedMap<String, Object> headers, OutputStream outputStream)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(outputStream, map);
    }
}
