package uk.gov.gsi.justice.alfresco.proxy.service;

import javax.inject.Inject;
import javax.inject.Named;
import uk.gov.gsi.justice.alfresco.proxy.av.AntivirusClient;
import uk.gov.gsi.justice.alfresco.proxy.model.ClamAvHealth;

@Named("ClamAvHealthChecker")
public class ClamAvHealthChecker implements DependencyHealthChecker<ClamAvHealth> {
  private final AntivirusClient antivirusClient;

  @Inject
  public ClamAvHealthChecker(final AntivirusClient antivirusClient) {
    this.antivirusClient = antivirusClient;
  }

  @Override
  public ClamAvHealth checkDependencyHealth() {
    return antivirusClient.checkHealth();
  }
}
