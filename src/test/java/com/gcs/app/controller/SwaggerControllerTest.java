package com.gcs.app.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SwaggerController.class)
@TestPropertySource(properties = {"app.api.base-path=/gym-crm-core/api/v1", "app.swagger.file-name=gym.yml", "metrics.enabled=false"})
class SwaggerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SwaggerController swaggerController;

    @Value("${app.api.base-path}")
    private String basePath;

    @Value("${app.swagger.file-name}")
    private String swaggerFileName;

    @Test
    void givenYamlFileExists_whenGetOpenApi_thenReturnsYamlContentWithCorrectContentType() throws Exception {
        ClassPathResource resource = new ClassPathResource(swaggerFileName);
        assertThat(resource.exists()).as("gym.yml must exist in test classpath").isTrue();

        String expectedContent = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        mockMvc.perform(get(basePath + "/openapi"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/x-yaml"))
                .andExpect(content().string(expectedContent));
    }
}