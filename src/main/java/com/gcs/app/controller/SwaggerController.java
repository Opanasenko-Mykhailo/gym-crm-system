package com.gcs.app.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.gcs.app.controller.ApiConstant.BASE_PATH;

@Controller
@RequestMapping(BASE_PATH)
public class SwaggerController {

    @GetMapping(value = "/openapi", produces = "application/x-yaml")
    public ResponseEntity<String> getSwaggerYaml() throws IOException {
        ClassPathResource yamlFile = new ClassPathResource("gym.yml");

        if (!yamlFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        try (var inputStream = yamlFile.getInputStream()) {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/x-yaml"))
                    .body(content);
        }
    }
}