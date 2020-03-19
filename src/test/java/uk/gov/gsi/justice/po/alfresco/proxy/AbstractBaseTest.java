package uk.gov.gsi.justice.po.alfresco.proxy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.MediaType;
import uk.gov.gsi.justice.po.alfresco.proxy.provider.TimestampProvider;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public abstract class AbstractBaseTest {
    protected final String serviceName = "hmpps-po-alfresco-proxy";
    protected final JsonReader jsonReader = new JsonReader();
    @Inject
    protected Gson gson;
    @Inject
    protected TimestampProvider timestampProvider;
    protected final Instant timestamp = Instant.now();
    protected final String alfrescoHealthEndpoint = "/alfresco/service/noms-spg/notificationStatus";
    protected JsonObject alfrescoNotificationStatus;
    protected final MediaType contentType = new MediaType(APPLICATION_JSON.getType(),
            APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    protected JsonObject alfrescoHealthCheckSampleResponse() throws IOException {
        final String notificationStatus = "thirdparty/responses/NotificationStatus.json";
        final String content = jsonReader.readFile(notificationStatus);
        return JsonParser.parseString(content).getAsJsonObject();
    }
}
