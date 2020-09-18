package uk.gov.gsi.justice.alfresco.proxy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.gsi.justice.alfresco.proxy.utils.ClamAvConnectionParametersProvider;
import uk.gov.gsi.justice.alfresco.proxy.utils.TimestampProvider;

import javax.inject.Inject;
import java.time.Instant;

public abstract class AbstractBaseTest {
    protected final String serviceName = "SPG Alfresco Proxy";
    protected final JsonReader jsonReader = new JsonReader();
    @Inject
    protected Gson gson;
    @Value("${alfresco.health.endpoint}")
    protected String alfrescoHealthEndpoint;
    @Inject
    protected TimestampProvider timestampProvider;
    @Inject
    protected ClamAvConnectionParametersProvider clamAvConnectionParametersProvider;
    protected final int clamAVTimeout = 60000;
    protected final Instant timestamp = Instant.now();
    protected final String stableText = "STABLE";
    protected JsonObject alfrescoStatus;
    protected final String apiHealthEndpoint = "/api/health";
}