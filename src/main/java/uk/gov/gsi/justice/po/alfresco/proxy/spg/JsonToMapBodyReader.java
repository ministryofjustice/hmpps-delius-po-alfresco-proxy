package uk.gov.gsi.justice.po.alfresco.proxy.spg;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Consumes(MediaType.APPLICATION_JSON)
@Provider
public class JsonToMapBodyReader implements MessageBodyReader<Map> {

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Map.class.isAssignableFrom(type);
    }

    public Map readFrom(Class<Map> clazz, Type type, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, String> headers, InputStream inputStream)
            throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        Map jsonMap = objectMapper.readValue(inputStream, Map.class);
        return jsonMap;
    }
}
