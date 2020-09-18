package uk.gov.gsi.justice.alfresco.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.gsi.justice.alfresco.proxy.model.ClamAvHealth;
import uk.gov.gsi.justice.alfresco.proxy.utils.ClamAvConnectionParametersProvider;
import xyz.capybara.clamav.ClamavClient;

import javax.inject.Inject;
import javax.inject.Named;

import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.FAULT;
import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.OK;

@Named("ClamAvHealthChecker")
public class ClamAvHealthChecker implements DependencyHealthChecker<ClamAvHealth> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClamAvHealthChecker.class);

    private final ClamAvConnectionParametersProvider clamAvConnectionParametersProvider;

    @Inject
    public ClamAvHealthChecker(final ClamAvConnectionParametersProvider clamAvConnectionParametersProvider) {
        this.clamAvConnectionParametersProvider = clamAvConnectionParametersProvider;
    }

    @Override
    public ClamAvHealth checkDependencyHealth() {
        try {
            final ClamavClient clamavClient = new ClamavClient(
                    clamAvConnectionParametersProvider.host(),
                    clamAvConnectionParametersProvider.port()
            );
            final String clamAvVersion = clamavClient.version();
            LOGGER.info("ClamAV version:: {}", clamAvVersion);
            return new ClamAvHealth(OK, clamAvVersion);
        } catch (Exception e) {
            LOGGER.error("Error getting ClamAV version::", e);
            return new ClamAvHealth(FAULT, e.getMessage());
        }
    }
}
