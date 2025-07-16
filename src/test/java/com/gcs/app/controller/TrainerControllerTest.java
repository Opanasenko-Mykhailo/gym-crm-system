package com.gcs.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcs.app.dto.AuthResponse;
import com.gcs.app.dto.StatusUpdateRequest;
import com.gcs.app.dto.TrainerProfileResponse;
import com.gcs.app.dto.TrainerRegistrationRequest;
import com.gcs.app.dto.TrainerUpdateRequest;
import com.gcs.app.dto.TrainingResponse;
import com.gcs.app.facade.GymFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
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
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setSpecializationId(1);

        AuthResponse response = new AuthResponse();
        response.setUsername("john.doe");
        response.setPassword("secret");

        when(gymFacade.createTrainer(any())).thenReturn(response);

        var result = mockMvc.perform(post("/api/v1/trainers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value("secret"));
    }

    @Test
    void testGetTrainerProfileSuccess() throws Exception {
        TrainerProfileResponse profile = new TrainerProfileResponse();
        profile.setUsername("john.doe");
        profile.setFirstName("John");
        profile.setLastName("Doe");

        when(gymFacade.getTrainerByUsername("john.doe")).thenReturn(profile);

        var result = mockMvc.perform(get("/api/v1/trainers/john.doe"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void testUpdateTrainerProfileSuccess() throws Exception {
        TrainerUpdateRequest request = new TrainerUpdateRequest();
        request.setFirstName("Updated");
        request.setLastName("Trainer");
        request.setIsActive(true);

        TrainerProfileResponse response = new TrainerProfileResponse();
        response.setUsername("john.doe");
        response.setFirstName("Updated");
        response.setLastName("Trainer");
        response.setIsActive(true);

        when(gymFacade.updateTrainer(any(), eq("john.doe"))).thenReturn(response);

        var result = mockMvc.perform(put("/api/v1/trainers/john.doe")
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

        var result = mockMvc.perform(patch("/api/v1/trainers/john.doe/change-activation-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());
        verify(gymFacade).setTrainerActive("john.doe", false);
    }

    @Test
    void testGetTrainerTrainingsSuccess() throws Exception {
        TrainingResponse training = new TrainingResponse();
        training.setTrainingName("Yoga");
        training.setTrainingDate(LocalDate.of(2025, 7, 15));
        training.setTrainingType("Stretching");
        training.setTrainingDuration(60);
        training.setTraineeName("Jane");

        when(gymFacade.getTrainerTrainings(any())).thenReturn(List.of(training));

        var result = mockMvc.perform(get("/api/v1/trainers/john.doe/trainings")
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
        var result = mockMvc.perform(get("/api/v1/trainers/john.doe/trainings")
                .param("periodFrom", "invalid-date"));

        result.andExpect(status().isBadRequest());
    }
}