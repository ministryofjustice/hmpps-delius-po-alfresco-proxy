package uk.gov.gsi.justice.alfresco.proxy.service;

import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.gsi.justice.alfresco.proxy.model.AlfrescoHealth;
import uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus;

import javax.inject.Named;
import javax.ws.rs.core.Response;

import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.FAULT;
import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.OK;

@Named("AlfrescoHealthChecker")
public class AlfrescoHealthChecker implements DependencyHealthChecker<AlfrescoHealth> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AlfrescoHealthChecker.class);
    private final WebClient alfrescoClient;

    public AlfrescoHealthChecker(@Value("${alfresco.base.url}") final String alfrescoBaseUrl,
                                 @Value("${alfresco.health.endpoint}") final String alfrescoHealthCheckPath) {
        alfrescoClient = WebClient.create(alfrescoBaseUrl + alfrescoHealthCheckPath);
    }

    @Override
    public AlfrescoHealth checkDependencyHealth() {
        try {
            final Response response = alfrescoClient.get();
            final int code = response.getStatus();

            final DependencyStatus status = code == 200 ? OK : FAULT;

            return new AlfrescoHealth(status, code, null);
        } catch (Exception e) {
            LOGGER.error("Error checking Alfresco health:::", e);
            return new AlfrescoHealth(FAULT, 0, e.getMessage());
        }
    }
}
