package uk.gov.gsi.justice.alfresco.proxy.utils;

public interface ClamAvConnectionParametersProvider {
    String host();
    int port();
    int timeout();
}
