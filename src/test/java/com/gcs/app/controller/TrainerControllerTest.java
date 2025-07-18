package com.gcs.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcs.app.facade.GymFacade;
import com.gcs.app.rest.ActivationStatusRequest;
import com.gcs.app.rest.TrainerCreateRequest;
import com.gcs.app.rest.TrainerGetResponse;
import com.gcs.app.rest.TrainerTrainingGetResponse;
import com.gcs.app.rest.TrainerUpdateRequest;
import com.gcs.app.rest.TrainerUpdateResponse;
import com.gcs.app.rest.UserCreationResponse;
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

import static com.gcs.app.controller.ApiConstant.BASE_PATH;
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
        TrainerCreateRequest request = new TrainerCreateRequest();
        request.setFirstName("Rowan");
        request.setLastName("Atkinson");
        request.specialization("Yoga");

        UserCreationResponse response = new UserCreationResponse();
        response.setUsername("rowan.atkinson");
        response.setPassword("secret");

        when(gymFacade.createTrainer(any())).thenReturn(response);

        var result = mockMvc.perform(post(BASE_PATH + "/trainers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("rowan.atkinson"))
                .andExpect(jsonPath("$.password").value("secret"));
    }

    @Test
    void testGetTrainerProfileSuccess() throws Exception {
        TrainerGetResponse profile = new TrainerGetResponse();
        profile.setUsername("rowan.atkinson");
        profile.setFirstName("Rowan");
        profile.setLastName("Atkinson");

        when(gymFacade.getTrainerByUsername("rowan.atkinson")).thenReturn(profile);

        var result = mockMvc.perform(get(BASE_PATH + "/trainers/rowan.atkinson"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("rowan.atkinson"))
                .andExpect(jsonPath("$.firstName").value("Rowan"))
                .andExpect(jsonPath("$.lastName").value("Atkinson"));
    }

    @Test
    void testUpdateTrainerProfileSuccess() throws Exception {
        TrainerUpdateRequest request = JsonReaderUtil.readFromJson(
                "json/trainer-update-request.json",
                TrainerUpdateRequest.class
        );

        TrainerUpdateResponse response = new TrainerUpdateResponse();
        response.setUsername("rowan.atkinson");
        response.setFirstName(request.getFirstName());
        response.setLastName(request.getLastName());
        response.setIsActive(request.getIsActive());

        when(gymFacade.updateTrainer(any(), eq("rowan.atkinson"))).thenReturn(response);

        var result = mockMvc.perform(put(BASE_PATH + "/trainers/rowan.atkinson")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Trainer"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void testChangeActivationStatusSuccess() throws Exception {
        ActivationStatusRequest request = new ActivationStatusRequest();
        request.setIsActive(false);

        var result = mockMvc.perform(patch(BASE_PATH + "/trainers/rowan.atkinson/change-activation-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());
        verify(gymFacade).setTrainerActive("rowan.atkinson", false);
    }

    @Test
    void testGetTrainerTrainingsSuccess() throws Exception {
        List<TrainerTrainingGetResponse> trainings = JsonReaderUtil.readFromJson("json/get-trainer-trainings-response.json",
                new TypeReference<List<TrainerTrainingGetResponse>>() {
                });

        when(gymFacade.getTrainerTrainings(any())).thenReturn(trainings);

        var result = mockMvc.perform(get(BASE_PATH + "/trainers/rowan.atkinson/trainings")
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
                .andExpect(jsonPath("$[0].traineeName").value("Jane"))
                .andExpect(jsonPath("$[1].trainingName").value("Pilates"))
                .andExpect(jsonPath("$[1].trainingDate[0]").value(2025))
                .andExpect(jsonPath("$[1].trainingDate[1]").value(7))
                .andExpect(jsonPath("$[1].trainingDate[2]").value(20))
                .andExpect(jsonPath("$[1].trainingType").value("Core"))
                .andExpect(jsonPath("$[1].trainingDuration").value(45))
                .andExpect(jsonPath("$[1].traineeName").value("Anna"));
    }

    @Test
    void testGetTrainerTrainingsInvalidDate() throws Exception {
        var result = mockMvc.perform(get(BASE_PATH + "/trainers/rowan.atkinson/trainings")
                .param("periodFrom", "invalid-date"));

        result.andExpect(status().isBadRequest());
    }
}