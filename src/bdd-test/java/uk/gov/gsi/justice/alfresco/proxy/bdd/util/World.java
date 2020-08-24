package uk.gov.gsi.justice.alfresco.proxy.bdd.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;

import javax.ws.rs.core.Response;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public enum World {
    INSTANCE;

    final WireMockServer wireMockServer = new WireMockServer(options()
            .port(6067)
            .notifier(new ConsoleNotifier(true)));

    private Response response;

    public WireMockServer getWireMockServer() {
        return wireMockServer;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(final Response response) {
        this.response = response;
    }
}
