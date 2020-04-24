package uk.gov.gsi.justice.po.alfresco.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = {"uk.gov.gsi.justice.po.alfresco.proxy"},
        exclude = {JacksonAutoConfiguration.class}
)
public class ApplicationBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationBootstrap.class, args);
    }
}