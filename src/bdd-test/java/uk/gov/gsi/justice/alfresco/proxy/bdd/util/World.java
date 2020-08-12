package uk.gov.gsi.justice.alfresco.proxy.bdd.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import kong.unirest.HttpResponse;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public enum World {
    INSTANCE;

    final WireMockServer wireMockServer = new WireMockServer(options()
            .port(6067)
            .notifier(new ConsoleNotifier(true)));

    private HttpResponse<String> responseEntity;

    public WireMockServer getWireMockServer() {
        return wireMockServer;
    }

    public HttpResponse<String> getResponseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(final HttpResponse<String> responseEntity) {
        this.responseEntity = responseEntity;
    }
}
