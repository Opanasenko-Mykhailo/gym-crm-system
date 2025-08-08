package com.gcs.app.controller.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.gcs.app.util.JsonReaderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

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

    @Test
    @WithAnonymousUser
    void register_shouldBeAccessibleWithoutAuth() throws Exception {
        String json = testData.get("trainerCreateRequest").toString();

        mockMvc.perform(post(basePath + "/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void getProfile_shouldBeUnauthorizedWithoutAuth() throws Exception {
        mockMvc.perform(get(basePath + "/trainers/" + USERNAME))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {"TRAINER"})
    void getProfile_shouldBeAccessibleForTrainer() throws Exception {
        mockMvc.perform(get(basePath + "/trainers/" + USERNAME))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "some.trainee", roles = {"TRAINEE"})
    void getProfile_shouldBeForbiddenForTrainee() throws Exception {
        mockMvc.perform(get(basePath + "/trainers/" + USERNAME))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void updateProfile_shouldBeUnauthorizedWithoutAuth() throws Exception {
        String json = testData.get("trainerUpdateRequest").toString();

        mockMvc.perform(put(basePath + "/trainers/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {"TRAINER"})
    void updateProfile_shouldBeAccessibleForOwnerTrainer() throws Exception {
        String json = testData.get("trainerUpdateRequest").toString();

        mockMvc.perform(put(basePath + "/trainers/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "other.trainer", roles = {"TRAINER"})
    void updateProfile_shouldBeForbiddenForOtherTrainer() throws Exception {
        String json = testData.get("trainerUpdateRequest").toString();

        mockMvc.perform(put(basePath + "/trainers/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "some.trainee", roles = {"TRAINEE"})
    void updateProfile_shouldBeForbiddenForTrainee() throws Exception {
        String json = testData.get("trainerUpdateRequest").toString();

        mockMvc.perform(put(basePath + "/trainers/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void changeActivationStatus_shouldBeUnauthorizedWithoutAuth() throws Exception {
        String json = testData.get("activationStatusRequest").toString();

        mockMvc.perform(patch(basePath + "/trainers/" + USERNAME + "/change-activation-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {"TRAINER"})
    void changeActivationStatus_shouldBeAccessibleForOwnerTrainer() throws Exception {
        String json = testData.get("activationStatusRequest").toString();

        mockMvc.perform(patch(basePath + "/trainers/" + USERNAME + "/change-activation-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "other.trainer", roles = {"TRAINER"})
    void changeActivationStatus_shouldBeForbiddenForOtherTrainer() throws Exception {
        String json = testData.get("activationStatusRequest").toString();

        mockMvc.perform(patch(basePath + "/trainers/" + USERNAME + "/change-activation-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "some.trainee", roles = {"TRAINEE"})
    void changeActivationStatus_shouldBeForbiddenForTrainee() throws Exception {
        String json = testData.get("activationStatusRequest").toString();

        mockMvc.perform(patch(basePath + "/trainers/" + USERNAME + "/change-activation-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void getTrainings_shouldBeUnauthorizedWithoutAuth() throws Exception {
        mockMvc.perform(get(basePath + "/trainers/" + USERNAME + "/trainings"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = USERNAME, roles = {"TRAINER"})
    void getTrainings_shouldBeAccessibleForTrainer() throws Exception {
        mockMvc.perform(get(basePath + "/trainers/" + USERNAME + "/trainings"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "some.trainee", roles = {"TRAINEE"})
    void getTrainings_shouldBeForbiddenForTrainee() throws Exception {
        mockMvc.perform(get(basePath + "/trainers/" + USERNAME + "/trainings"))
                .andExpect(status().isForbidden());
    }
}