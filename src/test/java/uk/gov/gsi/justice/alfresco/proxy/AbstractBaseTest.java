package uk.gov.gsi.justice.alfresco.proxy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import uk.gov.gsi.justice.alfresco.proxy.utils.TimestampProvider;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;

public abstract class AbstractBaseTest {
    protected final String serviceName = "SPG PO Alfresco Proxy";
    protected final JsonReader jsonReader = new JsonReader();
    @Inject
    protected Gson gson;
    @Inject
    protected TimestampProvider timestampProvider;
    protected final Instant timestamp = Instant.now();
    protected final String stableText = "STABLE";
    protected final String alfrescoHealthEndpoint = "/alfresco/service/noms-spg/notificationStatus";
    protected JsonObject alfrescoNotificationStatus;
    protected final String apiHealthEndpoint = "/api/healthcheck";

    protected JsonObject alfrescoHealthCheckSampleResponse() throws IOException {
        final String notificationStatus = "thirdparty/responses/NotificationStatus.json";
        final String content = jsonReader.readFile(notificationStatus);
        return JsonParser.parseString(content).getAsJsonObject();
    }
}
