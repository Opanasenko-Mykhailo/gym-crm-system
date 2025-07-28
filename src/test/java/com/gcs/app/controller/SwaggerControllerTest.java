package com.gcs.app.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static com.gcs.app.controller.ApiConstant.BASE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SwaggerControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SwaggerController()).build();
    }

    @Test
    void givenYamlFileExists_whenGetOpenApi_thenReturnsYamlContentWithCorrectContentType() throws Exception {
        ClassPathResource resource = new ClassPathResource("gym.yml");
        assertThat(resource.exists()).as("gym.yml must exist in test classpath").isTrue();
        String expectedContent = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        mockMvc.perform(get(BASE_PATH + "/openapi"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/x-yaml"))
                .andExpect(content().string(expectedContent));
    }

    @Test
    void givenYamlFileDoesNotExist_whenGetOpenApi_thenReturns404() throws Exception {
        SwaggerController controller = new SwaggerController() {
            @Override
            public ResponseEntity<String> getSwaggerYaml() {
                return ResponseEntity.notFound().build();
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get(BASE_PATH + "/openapi"))
                .andExpect(status().isNotFound());
    }
}