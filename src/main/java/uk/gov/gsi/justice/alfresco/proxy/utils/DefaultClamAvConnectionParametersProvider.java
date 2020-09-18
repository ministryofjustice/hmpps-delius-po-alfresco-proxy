package uk.gov.gsi.justice.alfresco.proxy.utils;

public class DefaultClamAvConnectionParametersProvider implements ClamAvConnectionParametersProvider {
    private final String address;
    private final int port;
    private final int timeout;

    public DefaultClamAvConnectionParametersProvider(String address, int port, int timeout) {
        this.address = address;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public String host() {
        return address;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public int timeout() {
        return timeout;
    }
}
