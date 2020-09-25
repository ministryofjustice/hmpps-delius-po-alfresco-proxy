package uk.gov.gsi.justice.alfresco.proxy.av;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.gsi.justice.alfresco.proxy.av.AntivirusResponse.Status;
import uk.gov.gsi.justice.alfresco.proxy.exceptions.AntivirusException;
import uk.gov.gsi.justice.alfresco.proxy.model.ClamAvHealth;
import uk.gov.gsi.justice.alfresco.proxy.utils.ClamAvConnectionParametersProvider;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.FAULT;
import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.OK;

@Named("antivirusClient")
public class AntivirusClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(AntivirusClient.class);

    private final ClamAvConnectionParametersProvider clamAvConnectionParametersProvider;

    @Inject
    public AntivirusClient(ClamAvConnectionParametersProvider clamAvConnectionParametersProvider) {
        this.clamAvConnectionParametersProvider = clamAvConnectionParametersProvider;
    }

    public ClamAvHealth checkHealth() {
        final String host = clamAvConnectionParametersProvider.host();
        final int port = clamAvConnectionParametersProvider.port();
        LOGGER.info("============================== Connecting to ClamAV with the following parameters ==============================");
        LOGGER.info("Host: {}", host);
        LOGGER.info("Port: {}", port);
        LOGGER.info("================================================================================================================");

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

    public AntivirusResponse scan(InputStream is) {
        final String host = clamAvConnectionParametersProvider.host();
        final int port = clamAvConnectionParametersProvider.port();

        final ClamavClient clamavClient = new ClamavClient(
                clamAvConnectionParametersProvider.host(),
                clamAvConnectionParametersProvider.port()
        );

        final String clamAvVersion = clamavClient.version();
        LOGGER.info("============================== Connecting to ClamAV with the following parameters ==============================");
        LOGGER.info("Host: {}", host);
        LOGGER.info("Port: {}", port);
        LOGGER.info("ClamAV Version: {}", clamAvVersion);
        LOGGER.info("================================================================================================================");
        try {
            final ScanResult scanResult = clamavClient.scan(is);

            if (scanResult instanceof ScanResult.OK) {
                return new AntivirusResponse(Status.PASSED);
            }

            if (scanResult instanceof ScanResult.VirusFound) {
                final Map<String, Collection<String>> viruses = ((ScanResult.VirusFound) scanResult).getFoundViruses();
                final AntivirusException antivirusException = new AntivirusException("Blacklisted: " + viruses + " FOUND");
                return new AntivirusResponse(Status.FAILED, antivirusException);
            }

            final AntivirusException antivirusException = new AntivirusException("Response from Antivirus was null");
            return new AntivirusResponse(Status.ERROR, antivirusException);
        } catch (Exception e) {
            LOGGER.error("Could not connect to ClamAV", e);
            final AntivirusException antivirusException = new AntivirusException("Could not connect to ClamAV", e);
            return new AntivirusResponse(antivirusException);
        }
    }
}