package uk.gov.gsi.justice.po.alfresco.proxy.controller;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.gsi.justice.po.alfresco.proxy.dto.HealthCheckResponse;
import uk.gov.gsi.justice.po.alfresco.proxy.service.HealthCheckService;

import javax.inject.Inject;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
public class AdminController {
    @Inject
    private HealthCheckService healthCheckService;
    @Inject
    private Gson gson;

    @RequestMapping(
            method = GET,
            path = "/healthcheck",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HealthCheckResponse> healthCheck() {
        final HealthCheckResponse healthCheckResponse = healthCheckService.checkHealth();

        return new ResponseEntity<>(
                healthCheckResponse,
                HttpStatus.OK
        );
    }
}
