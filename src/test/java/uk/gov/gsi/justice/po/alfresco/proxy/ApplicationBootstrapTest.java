package uk.gov.gsi.justice.po.alfresco.proxy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.gsi.justice.po.alfresco.proxy.bdd.ioc.TestConfig;
import uk.gov.gsi.justice.po.alfresco.proxy.ioc.AppConfig;

import javax.inject.Inject;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"application.name=ApplicationBootstrapTest", "alfresco.base.url=http://localhost:6067", "alfresco.health.endpoint=/alfresco/service/noms-spg/notificationStatus"},
        classes = {AppConfig.class, TestConfig.class, ApplicationBootstrap.class})
@AutoConfigureMockMvc
class ApplicationBootstrapTest {
    @Inject
    private MockMvc mockMvc;

    @Test
    public void testReturnSpringActuatorInfo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/info")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()));
    }
}