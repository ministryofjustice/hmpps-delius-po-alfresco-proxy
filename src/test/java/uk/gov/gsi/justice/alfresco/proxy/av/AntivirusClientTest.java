package uk.gov.gsi.justice.alfresco.proxy.av;

import org.junit.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import uk.gov.gsi.justice.alfresco.proxy.TestFileReader;
import uk.gov.gsi.justice.alfresco.proxy.exceptions.AntivirusException;
import uk.gov.gsi.justice.alfresco.proxy.model.ClamAvHealth;
import uk.gov.gsi.justice.alfresco.proxy.utils.ClamAvConnectionParametersProvider;

import java.io.InputStream;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;

import static java.util.Collections.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.FAULT;
import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.OK;

public class AntivirusClientTest {
    private final String clamAvImage = "quay.io/ukhomeofficedigital/clamav:latest";
    private final int clamAvPort = 3310;

    @SuppressWarnings("rawtypes")
    private final GenericContainer clamAv = new GenericContainer<>(clamAvImage)
            .withExposedPorts(clamAvPort)
            .waitingFor(Wait.forListeningPort()
                    .withStartupTimeout(Duration.ofMinutes(5)));

    private final ClamAvConnectionParametersProvider clamAvConnectionParametersProvider = mock(ClamAvConnectionParametersProvider.class);
    private final TestFileReader fileReader = new TestFileReader();

    private final AntivirusClient sut = new AntivirusClient(clamAvConnectionParametersProvider);

    @Before
    public void prepare() {
        if (!clamAv.isRunning()) {
            clamAv.start();
        }

        when(clamAvConnectionParametersProvider.host()).thenReturn(clamAv.getContainerIpAddress());
        when(clamAvConnectionParametersProvider.port()).thenReturn(clamAv.getFirstMappedPort());
    }

    @After
    public void tearDown() {
        stopClamAV();
    }

    @Test
    public void testGetClamAvVersion() {
        final ClamAvHealth expectedResult = new ClamAvHealth(OK, "ClamAV 0.102.1/25722/Thu Feb 13 11:45:05 2020");

        final ClamAvHealth actualResult = sut.checkHealth();

        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void testGetClamAvVersionWhenClamAvIsNotReachable() {
        stopClamAV();
        when(clamAvConnectionParametersProvider.host()).thenReturn("100.90.80.70");
        when(clamAvConnectionParametersProvider.port()).thenReturn(1234);

        final ClamAvHealth expectedResult = new ClamAvHealth(FAULT, "xyz.capybara.clamav.CommunicationException: Error while communicating with the server");

        final ClamAvHealth actualResult = sut.checkHealth();

        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void testScanVirusCompromisedDocument() throws Exception {
        final Map<String, Collection<String>> viruses = singletonMap("stream", singleton("Win.Test.EICAR_HDB-1"));
        final AntivirusException antivirusException = new AntivirusException("Blacklisted: " + viruses + " FOUND");
        final AntivirusResponse expectedResponse = new AntivirusResponse(AntivirusResponse.Status.FAILED, antivirusException);
        final InputStream fileAsStream = fileReader.getFileAsStream("documents/eicar.txt");

        final AntivirusResponse actualResponse = sut.scan(fileAsStream);

        assertThat(actualResponse, is(expectedResponse));
    }

    @Test
    public void testScanCleanDocument() throws Exception {
        final AntivirusResponse expectedResponse = new AntivirusResponse(AntivirusResponse.Status.PASSED);
        final InputStream fileAsStream = fileReader.getFileAsStream("documents/no-virus.txt");

        final AntivirusResponse actualResponse = sut.scan(fileAsStream);

        assertThat(actualResponse, is(expectedResponse));
    }

    @Test
    public void testScanDocumentWhenClamAvIsNotReachable() throws Exception {
        stopClamAV();
        final AntivirusException antivirusException = new AntivirusException("Could not connect to ClamAV");
        final AntivirusResponse expectedResponse = new AntivirusResponse(AntivirusResponse.Status.ERROR, antivirusException);
        final InputStream fileAsStream = fileReader.getFileAsStream("documents/no-virus.txt");

        final AntivirusResponse actualResponse = sut.scan(fileAsStream);

        assertThat(actualResponse, is(expectedResponse));
    }

    private void stopClamAV() {
        if (clamAv.isRunning()) {
            clamAv.stop();
        }
    }
}