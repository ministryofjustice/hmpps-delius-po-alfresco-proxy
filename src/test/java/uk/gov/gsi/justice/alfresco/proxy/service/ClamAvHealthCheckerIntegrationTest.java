package uk.gov.gsi.justice.alfresco.proxy.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import uk.gov.gsi.justice.alfresco.proxy.av.AntivirusClient;
import uk.gov.gsi.justice.alfresco.proxy.model.ClamAvHealth;
import uk.gov.gsi.justice.alfresco.proxy.utils.ClamAvConnectionParametersProvider;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.FAULT;
import static uk.gov.gsi.justice.alfresco.proxy.model.DependencyStatus.OK;

public class ClamAvHealthCheckerIntegrationTest {
    private final String clamAVImage = "quay.io/ukhomeofficedigital/clamav:latest";
    private final int clamAVPort = 3310;

    @SuppressWarnings("rawtypes")
    private final GenericContainer clamAV = new GenericContainer<>(clamAVImage)
            .withExposedPorts(clamAVPort)
            .waitingFor(Wait.forListeningPort()
                    .withStartupTimeout(Duration.ofMinutes(5)));

    private final ClamAvConnectionParametersProvider clamAvConnectionParametersProvider = mock(ClamAvConnectionParametersProvider.class);
    private final AntivirusClient antivirusClient = new AntivirusClient(clamAvConnectionParametersProvider);

    private final ClamAvHealthChecker sut = new ClamAvHealthChecker(antivirusClient);

    @Before
    public void setUp() throws Exception {
        if (clamAV.isRunning()) {
            clamAV.stop();
            SECONDS.sleep(5);
        }
    }

    @After
    public void tearDown() {
        if (clamAV.isRunning()) {
            clamAV.stop();
        }
    }

    @Test
    public void testClamAvHealthCheck() {
        clamAV.start();
        when(clamAvConnectionParametersProvider.host()).thenReturn(clamAV.getContainerIpAddress());
        when(clamAvConnectionParametersProvider.port()).thenReturn(clamAV.getFirstMappedPort());

        final ClamAvHealth expectedResult = new ClamAvHealth(OK, "ClamAV 0.102.1/25722/Thu Feb 13 11:45:05 2020");

        final ClamAvHealth actualResult = sut.checkDependencyHealth();

        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void testClamAvHealthCheckFailsWhenClamAvIsNotReachable() {
        when(clamAvConnectionParametersProvider.host()).thenReturn("100.90.80.70");
        when(clamAvConnectionParametersProvider.port()).thenReturn(1234);

        final ClamAvHealth expectedResult = new ClamAvHealth(FAULT, "xyz.capybara.clamav.CommunicationException: Error while communicating with the server");

        final ClamAvHealth actualResult = sut.checkDependencyHealth();

        assertThat(actualResult, is(expectedResult));
    }
}