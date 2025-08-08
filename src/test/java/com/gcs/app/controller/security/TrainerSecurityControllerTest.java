package com.gcs.app.controller.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.gcs.app.util.JsonReaderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TrainerSecurityControllerTest extends AbstractSecurityControllerTest {

    private static final String USERNAME = "rowan.atkinson";
    private static final String TEST_DATA_PATH = "json/security/trainer-test-data.json";

    private static JsonNode testData;

    @BeforeAll
    static void setup() {
        testData = JsonReaderUtil.readFromJson(TEST_DATA_PATH, JsonNode.class);
    }

    @DisplayName("Register should be accessible without authentication")
    @Test
    void register_shouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(post(basePath + "/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getCreateJson()))
                .andExpect(status().isOk());
    }

    @DisplayName("Unauthorized requests without authentication")
    @ParameterizedTest(name = "{index} => method={0}, endpoint={1}")
    @CsvSource({
            "GET, /trainers/" + USERNAME,
            "PUT, /trainers/" + USERNAME,
            "PATCH, /trainers/" + USERNAME + "/change-activation-status",
            "GET, /trainers/" + USERNAME + "/trainings"
    })
    void request_shouldBeUnauthorizedWithoutAuth(String method, String endpoint) throws Exception {
        ResultActions action = mockMvc.perform(
                switch (method) {
                    case "GET" -> get(basePath + endpoint);
                    case "PUT" -> put(basePath + endpoint)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(getContentForEndpoint(endpoint));
                    case "PATCH" -> patch(basePath + endpoint)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(getContentForEndpoint(endpoint));
                    default -> throw new IllegalArgumentException("Unsupported method " + method);
                }
        );
        action.andExpect(status().isUnauthorized());
    }

    @DisplayName("Access control tests for different users, roles and endpoints")
    @ParameterizedTest(name = "{index} => username={0}, role={1}, method={2}, endpoint={3}, expectedStatus={4}")
    @CsvSource(delimiter = ';', value = {
            "rowan.atkinson; TRAINER; GET; /trainers/rowan.atkinson; 200",
            "some.trainee; TRAINEE; GET; /trainers/rowan.atkinson; 403",

            "rowan.atkinson; TRAINER; PUT; /trainers/rowan.atkinson; 200",
            "other.trainer; TRAINER; PUT; /trainers/rowan.atkinson; 403",
            "some.trainee; TRAINEE; PUT; /trainers/rowan.atkinson; 403",

            "rowan.atkinson; TRAINER; PATCH; /trainers/rowan.atkinson/change-activation-status; 200",
            "other.trainer; TRAINER; PATCH; /trainers/rowan.atkinson/change-activation-status; 403",
            "some.trainee; TRAINEE; PATCH; /trainers/rowan.atkinson/change-activation-status; 403",

            "rowan.atkinson; TRAINER; GET; /trainers/rowan.atkinson/trainings; 200",
            "some.trainee; TRAINEE; GET; /trainers/rowan.atkinson/trainings; 403"
    })
    void accessControlTests(String username, String role, String method, String endpoint, int expectedStatus) throws Exception {
        ResultActions action = mockMvc.perform(
                switch (method) {
                    case "GET" -> get(basePath + endpoint)
                            .with(SecurityMockMvcRequestPostProcessors.user(username).roles(role));
                    case "PUT" -> put(basePath + endpoint)
                            .with(SecurityMockMvcRequestPostProcessors.user(username).roles(role))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(getContentForEndpoint(endpoint));
                    case "PATCH" -> patch(basePath + endpoint)
                            .with(SecurityMockMvcRequestPostProcessors.user(username).roles(role))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(getContentForEndpoint(endpoint));
                    default -> throw new IllegalArgumentException("Unsupported method " + method);
                }
        );

        action.andExpect(status().is(expectedStatus));
    }

    private String getCreateJson() {
        return testData.get("trainerCreateRequest").toString();
    }

    private String getUpdateJson() {
        return testData.get("trainerUpdateRequest").toString();
    }

    private String getActivationJson() {
        return testData.get("activationStatusRequest").toString();
    }

    private String getContentForEndpoint(String endpoint) {
        if (endpoint.endsWith("/change-activation-status")) {
            return getActivationJson();

        } else if (endpoint.equals("/trainers/" + USERNAME)) {
            return getUpdateJson();

        } else {
            return "";
        }
    }
}