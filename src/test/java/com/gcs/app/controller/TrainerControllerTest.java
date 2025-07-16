package com.gcs.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcs.app.facade.GymFacade;
import com.gcs.app.rest.AuthResponse;
import com.gcs.app.rest.StatusUpdateRequest;
import com.gcs.app.rest.TrainerProfileResponse;
import com.gcs.app.rest.TrainerRegistrationRequest;
import com.gcs.app.rest.TrainerUpdateRequest;
import com.gcs.app.rest.TrainingResponse;
import com.gcs.app.util.JsonReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String basePath = "/api/v1";

    private MockMvc mockMvc;

    @Mock
    private GymFacade gymFacade;

    @InjectMocks
    private TrainerController trainerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainerController).build();
    }

    @Test
    void testRegisterTrainerSuccess() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("Rowan");
        request.setLastName("Atkinson");
        request.setSpecializationId(1);

        AuthResponse response = new AuthResponse();
        response.setUsername("rowan.atkinson");
        response.setPassword("secret");

        when(gymFacade.createTrainer(any())).thenReturn(response);

        var result = mockMvc.perform(post(basePath + "/trainers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("rowan.atkinson"))
                .andExpect(jsonPath("$.password").value("secret"));
    }

    @Test
    void testGetTrainerProfileSuccess() throws Exception {
        TrainerProfileResponse profile = new TrainerProfileResponse();
        profile.setUsername("rowan.atkinson");
        profile.setFirstName("Rowan");
        profile.setLastName("Atkinson");

        when(gymFacade.getTrainerByUsername("rowan.atkinson")).thenReturn(profile);

        var result = mockMvc.perform(get(basePath + "/trainers/rowan.atkinson"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("rowan.atkinson"))
                .andExpect(jsonPath("$.firstName").value("Rowan"))
                .andExpect(jsonPath("$.lastName").value("Atkinson"));
    }

    @Test
    void testUpdateTrainerProfileSuccess() throws Exception {
        TrainerUpdateRequest request = new TrainerUpdateRequest();
        request.setFirstName("Updated");
        request.setLastName("Trainer");
        request.setIsActive(true);

        TrainerProfileResponse response = new TrainerProfileResponse();
        response.setUsername("rowan.atkinson");
        response.setFirstName("Updated");
        response.setLastName("Trainer");
        response.setIsActive(true);

        when(gymFacade.updateTrainer(any(), eq("rowan.atkinson"))).thenReturn(response);

        var result = mockMvc.perform(put(basePath + "/trainers/rowan.atkinson")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Trainer"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void testChangeActivationStatusSuccess() throws Exception {
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setIsActive(false);

        var result = mockMvc.perform(patch(basePath + "/trainers/rowan.atkinson/change-activation-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());
        verify(gymFacade).setTrainerActive("rowan.atkinson", false);
    }

    @Test
    void testGetTrainerTrainingsSuccess() throws Exception {
        TrainingResponse training = JsonReaderUtil.readFromJson("json/training-response.json", TrainingResponse.class);

        when(gymFacade.getTrainerTrainings(any())).thenReturn(List.of(training));

        var result = mockMvc.perform(get(basePath + "/trainers/rowan.atkinson/trainings")
                .param("periodFrom", "2025-07-01")
                .param("periodTo", "2025-07-31")
                .param("traineeName", "Jane"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Yoga"))
                .andExpect(jsonPath("$[0].trainingDate[0]").value(2025))
                .andExpect(jsonPath("$[0].trainingDate[1]").value(7))
                .andExpect(jsonPath("$[0].trainingDate[2]").value(15))
                .andExpect(jsonPath("$[0].trainingType").value("Stretching"))
                .andExpect(jsonPath("$[0].trainingDuration").value(60))
                .andExpect(jsonPath("$[0].traineeName").value("Jane"));
    }

    @Test
    void testGetTrainerTrainingsInvalidDate() throws Exception {
        var result = mockMvc.perform(get(basePath + "/trainers/rowan.atkinson/trainings")
                .param("periodFrom", "invalid-date"));

        result.andExpect(status().isBadRequest());
    }
}