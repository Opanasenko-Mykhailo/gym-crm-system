package com.gcs.app.controller.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.gcs.app.util.JsonReaderUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

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

    @Test
    @WithAnonymousUser
    void register_shouldBeAccessibleWithoutAuth() throws Exception {
        String createJson = testData.get("traineeCreateRequest").toString();

        mockMvc.perform(post(basePath + "/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void getProfile_shouldBeUnauthorizedWithoutAuth() throws Exception {
        mockMvc.perform(get(basePath + "/trainees/" + TRAINEE_USERNAME))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = TRAINEE_USERNAME, roles = {"TRAINEE"})
    void getProfile_shouldBeAccessibleForTrainee() throws Exception {
        mockMvc.perform(get(basePath + "/trainees/" + TRAINEE_USERNAME))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "trainer.user", roles = {"TRAINER"})
    void getProfile_shouldBeAccessibleForTrainer() throws Exception {
        mockMvc.perform(get(basePath + "/trainees/" + TRAINEE_USERNAME))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void updateProfile_shouldBeUnauthorizedWithoutAuth() throws Exception {
        String updateJson = testData.get("traineeUpdateRequest").toString();

        mockMvc.perform(put(basePath + "/trainees/" + TRAINEE_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = TRAINEE_USERNAME, roles = {"TRAINEE"})
    void updateProfile_shouldBeAccessibleForOwnerTrainee() throws Exception {
        String updateJson = testData.get("traineeUpdateRequest").toString();

        mockMvc.perform(put(basePath + "/trainees/" + TRAINEE_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "other.user", roles = {"TRAINEE"})
    void updateProfile_shouldBeForbiddenForOtherTrainee() throws Exception {
        String updateJson = testData.get("traineeUpdateRequest").toString();

        mockMvc.perform(put(basePath + "/trainees/" + TRAINEE_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "trainer.user", roles = {"TRAINER"})
    void updateProfile_shouldBeForbiddenForTrainer() throws Exception {
        String updateJson = testData.get("traineeUpdateRequest").toString();

        mockMvc.perform(put(basePath + "/trainees/" + TRAINEE_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void deleteProfile_shouldBeUnauthorizedWithoutAuth() throws Exception {
        mockMvc.perform(delete(basePath + "/trainees/" + TRAINEE_USERNAME))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = TRAINEE_USERNAME, roles = {"TRAINEE"})
    void deleteProfile_shouldBeAccessibleForOwnerTrainee() throws Exception {
        mockMvc.perform(delete(basePath + "/trainees/" + TRAINEE_USERNAME))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "other.user", roles = {"TRAINEE"})
    void deleteProfile_shouldBeForbiddenForOtherTrainee() throws Exception {
        mockMvc.perform(delete(basePath + "/trainees/" + TRAINEE_USERNAME))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "trainer.user", roles = {"TRAINER"})
    void deleteProfile_shouldBeForbiddenForTrainer() throws Exception {
        mockMvc.perform(delete(basePath + "/trainees/" + TRAINEE_USERNAME))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void changeActivationStatus_shouldBeUnauthorizedWithoutAuth() throws Exception {
        String activationJson = testData.get("activationStatusRequest").toString();

        mockMvc.perform(patch(basePath + "/trainees/" + TRAINEE_USERNAME + "/change-activation-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activationJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "trainer.user", roles = {"TRAINER"})
    void changeActivationStatus_shouldBeAccessibleForTrainer() throws Exception {
        String activationJson = testData.get("activationStatusRequest").toString();

        mockMvc.perform(patch(basePath + "/trainees/" + TRAINEE_USERNAME + "/change-activation-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activationJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = TRAINEE_USERNAME, roles = {"TRAINEE"})
    void changeActivationStatus_shouldBeForbiddenForTrainee() throws Exception {
        String activationJson = testData.get("activationStatusRequest").toString();

        mockMvc.perform(patch(basePath + "/trainees/" + TRAINEE_USERNAME + "/change-activation-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activationJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void getAvailableTrainers_shouldBeUnauthorizedWithoutAuth() throws Exception {
        mockMvc.perform(get(basePath + "/trainees/" + TRAINEE_USERNAME + "/available-trainers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = TRAINEE_USERNAME, roles = {"TRAINEE"})
    void getAvailableTrainers_shouldBeAccessibleForOwnerTrainee() throws Exception {
        mockMvc.perform(get(basePath + "/trainees/" + TRAINEE_USERNAME + "/available-trainers"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "other.user", roles = {"TRAINEE"})
    void getAvailableTrainers_shouldBeForbiddenForOtherTrainee() throws Exception {
        mockMvc.perform(get(basePath + "/trainees/" + TRAINEE_USERNAME + "/available-trainers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "trainer.user", roles = {"TRAINER"})
    void getAvailableTrainers_shouldBeForbiddenForTrainer() throws Exception {
        mockMvc.perform(get(basePath + "/trainees/" + TRAINEE_USERNAME + "/available-trainers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void updateTraineeTrainers_shouldBeUnauthorizedWithoutAuth() throws Exception {
        String trainersUpdateJson = testData.get("traineeAssignedTrainersUpdateRequest").toString();

        mockMvc.perform(put(basePath + "/trainees/" + TRAINEE_USERNAME + "/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainersUpdateJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = TRAINEE_USERNAME, roles = {"TRAINEE"})
    void updateTraineeTrainers_shouldBeAccessibleForOwnerTrainee() throws Exception {
        String trainersUpdateJson = testData.get("traineeAssignedTrainersUpdateRequest").toString();

        mockMvc.perform(put(basePath + "/trainees/" + TRAINEE_USERNAME + "/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainersUpdateJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "other.user", roles = {"TRAINEE"})
    void updateTraineeTrainers_shouldBeForbiddenForOtherTrainee() throws Exception {
        String trainersUpdateJson = testData.get("traineeAssignedTrainersUpdateRequest").toString();

        mockMvc.perform(put(basePath + "/trainees/" + TRAINEE_USERNAME + "/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainersUpdateJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "trainer.user", roles = {"TRAINER"})
    void updateTraineeTrainers_shouldBeForbiddenForTrainer() throws Exception {
        String trainersUpdateJson = testData.get("traineeAssignedTrainersUpdateRequest").toString();

        mockMvc.perform(put(basePath + "/trainees/" + TRAINEE_USERNAME + "/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainersUpdateJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void getTraineeTrainings_shouldBeUnauthorizedWithoutAuth() throws Exception {
        mockMvc.perform(get(basePath + "/trainees/" + TRAINEE_USERNAME + "/trainings"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = TRAINEE_USERNAME, roles = {"TRAINEE"})
    void getTraineeTrainings_shouldBeAccessibleForTrainee() throws Exception {
        mockMvc.perform(get(basePath + "/trainees/" + TRAINEE_USERNAME + "/trainings"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "trainer.user", roles = {"TRAINER"})
    void getTraineeTrainings_shouldBeAccessibleForTrainer() throws Exception {
        mockMvc.perform(get(basePath + "/trainees/" + TRAINEE_USERNAME + "/trainings"))
                .andExpect(status().isOk());
    }
}