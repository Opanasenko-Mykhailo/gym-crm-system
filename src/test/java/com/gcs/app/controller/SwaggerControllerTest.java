package com.gcs.app.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SwaggerControllerTest extends AbstractControllerTest {

    @Value("${app.swagger.file-name}")
    private String swaggerFileName;

    @Test
    @WithAnonymousUser
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