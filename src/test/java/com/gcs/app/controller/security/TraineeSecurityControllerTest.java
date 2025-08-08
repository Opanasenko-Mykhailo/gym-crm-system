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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TraineeSecurityControllerTest extends AbstractSecurityControllerTest {

    private static final String TRAINEE_USERNAME = "oleksandr.kovalenko";
    private static final String TEST_DATA_PATH = "json/security/trainee-test-data.json";

    private static JsonNode testData;

    @BeforeAll
    static void init() {
        testData = JsonReaderUtil.readFromJson(TEST_DATA_PATH, JsonNode.class);
    }

    @DisplayName("Register should be accessible without authentication")
    @Test
    void register_shouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(post(basePath + "/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getCreateJson()))
                .andExpect(status().isOk());
    }

    @DisplayName("Unauthorized requests without authentication")
    @ParameterizedTest(name = "{index} => method={0}, endpoint={1}")
    @CsvSource({
            "GET, /trainees/" + TRAINEE_USERNAME,
            "PUT, /trainees/" + TRAINEE_USERNAME,
            "DELETE, /trainees/" + TRAINEE_USERNAME,
            "PATCH, /trainees/" + TRAINEE_USERNAME + "/change-activation-status",
            "GET, /trainees/" + TRAINEE_USERNAME + "/available-trainers",
            "PUT, /trainees/" + TRAINEE_USERNAME + "/trainers",
            "GET, /trainees/" + TRAINEE_USERNAME + "/trainings"
    })
    void request_shouldBeUnauthorizedWithoutAuth(String method, String endpoint) throws Exception {
        ResultActions action = mockMvc.perform(
                switch (method) {
                    case "GET" -> get(basePath + endpoint);
                    case "PUT" ->
                            put(basePath + endpoint).contentType(MediaType.APPLICATION_JSON).content(getContentForEndpoint(endpoint));
                    case "DELETE" -> delete(basePath + endpoint);
                    case "PATCH" ->
                            patch(basePath + endpoint).contentType(MediaType.APPLICATION_JSON).content(getContentForEndpoint(endpoint));
                    default -> throw new IllegalArgumentException("Unsupported method " + method);
                }
        );
        action.andExpect(status().isUnauthorized());
    }

    @DisplayName("Access control for different users and roles on various endpoints")
    @ParameterizedTest(name = "{index} => username={0}, role={1}, method={2}, endpoint={3}, expectedStatus={4}")
    @CsvSource(delimiter = ';', value = {
            "oleksandr.kovalenko; TRAINEE; GET; /trainees/oleksandr.kovalenko; 200",
            "trainer.user; TRAINER; GET; /trainees/oleksandr.kovalenko; 200",
            "other.user; TRAINEE; GET; /trainees/oleksandr.kovalenko; 200",

            "oleksandr.kovalenko; TRAINEE; PUT; /trainees/oleksandr.kovalenko; 200",
            "trainer.user; TRAINER; PUT; /trainees/oleksandr.kovalenko; 403",
            "other.user; TRAINEE; PUT; /trainees/oleksandr.kovalenko; 403",

            "oleksandr.kovalenko; TRAINEE; DELETE; /trainees/oleksandr.kovalenko; 200",
            "trainer.user; TRAINER; DELETE; /trainees/oleksandr.kovalenko; 403",
            "other.user; TRAINEE; DELETE; /trainees/oleksandr.kovalenko; 403",

            "trainer.user; TRAINER; PATCH; /trainees/oleksandr.kovalenko/change-activation-status; 200",
            "oleksandr.kovalenko; TRAINEE; PATCH; /trainees/oleksandr.kovalenko/change-activation-status; 403",
            "other.user; TRAINEE; PATCH; /trainees/oleksandr.kovalenko/change-activation-status; 403",

            "oleksandr.kovalenko; TRAINEE; GET; /trainees/oleksandr.kovalenko/available-trainers; 200",
            "trainer.user; TRAINER; GET; /trainees/oleksandr.kovalenko/available-trainers; 403",
            "other.user; TRAINEE; GET; /trainees/oleksandr.kovalenko/available-trainers; 403",

            "oleksandr.kovalenko; TRAINEE; PUT; /trainees/oleksandr.kovalenko/trainers; 200",
            "trainer.user; TRAINER; PUT; /trainees/oleksandr.kovalenko/trainers; 403",
            "other.user; TRAINEE; PUT; /trainees/oleksandr.kovalenko/trainers; 403",

            "oleksandr.kovalenko; TRAINEE; GET; /trainees/oleksandr.kovalenko/trainings; 200",
            "trainer.user; TRAINER; GET; /trainees/oleksandr.kovalenko/trainings; 200",
            "other.user; TRAINEE; GET; /trainees/oleksandr.kovalenko/trainings; 200"
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
                    case "DELETE" -> delete(basePath + endpoint)
                            .with(SecurityMockMvcRequestPostProcessors.user(username).roles(role));
                    case "PATCH" -> patch(basePath + endpoint)
                            .with(SecurityMockMvcRequestPostProcessors.user(username).roles(role))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(getContentForEndpoint(endpoint));
                    default -> throw new IllegalArgumentException("Unsupported method " + method);
                }
        );

        action.andExpect(status().is(expectedStatus));
    }

    private String getUpdateJson() {
        return testData.get("traineeUpdateRequest").toString();
    }

    private String getCreateJson() {
        return testData.get("traineeCreateRequest").toString();
    }

    private String getActivationJson() {
        return testData.get("activationStatusRequest").toString();
    }

    private String getTrainersUpdateJson() {
        return testData.get("traineeAssignedTrainersUpdateRequest").toString();
    }

    private String getContentForEndpoint(String endpoint) {
        if (endpoint.endsWith("/change-activation-status")) {
            return getActivationJson();

        } else if (endpoint.endsWith("/trainers")) {
            return getTrainersUpdateJson();

        } else if (endpoint.equals("/trainees/" + TRAINEE_USERNAME)) {
            return getUpdateJson();

        } else {
            return "";
        }
    }
}