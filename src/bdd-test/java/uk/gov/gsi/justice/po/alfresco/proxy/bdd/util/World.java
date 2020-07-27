package uk.gov.gsi.justice.po.alfresco.proxy.bdd.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import kong.unirest.HttpResponse;
import org.springframework.cloud.contract.wiremock.WireMockSpring;

public enum World {
    INSTANCE;

    final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options()
            .port(6067)
            .notifier(new ConsoleNotifier(true)));

    private HttpResponse<String> responseEntity;

    public WireMockServer getWireMockServer() {
        return wireMockServer;
    }

    public HttpResponse<String> getResponseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(HttpResponse<String> responseEntity) {
        this.responseEntity = responseEntity;
    }
}
