package uk.gov.gsi.justice.alfresco.proxy.av;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.gsi.justice.alfresco.proxy.av.AntivirusResponse.Status;
import uk.gov.gsi.justice.alfresco.proxy.exceptions.AntivirusException;
import uk.gov.gsi.justice.alfresco.proxy.utils.ClamAvConnectionParametersProvider;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

@Named("antivirusClient")
public class AntivirusClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(AntivirusClient.class);

    private final ClamAvConnectionParametersProvider clamAvConnectionParametersProvider;

    @Inject
    public AntivirusClient(ClamAvConnectionParametersProvider clamAvConnectionParametersProvider) {
        this.clamAvConnectionParametersProvider = clamAvConnectionParametersProvider;
    }

    public AntivirusResponse scan(InputStream is) {
        final ClamavClient clamavClient = new ClamavClient(
                clamAvConnectionParametersProvider.host(),
                clamAvConnectionParametersProvider.port()
        );
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