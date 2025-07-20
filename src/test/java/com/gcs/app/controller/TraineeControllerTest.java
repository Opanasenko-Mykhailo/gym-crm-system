package com.gcs.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gcs.app.facade.GymFacade;
import com.gcs.app.rest.ActivationStatusRequest;
import com.gcs.app.rest.AvailableTrainerGetResponse;
import com.gcs.app.rest.TraineeAssignedTrainersUpdateRequest;
import com.gcs.app.rest.TraineeAssignedTrainersUpdateResponse;
import com.gcs.app.rest.TraineeAssignedTrainersUpdateResponseTrainersInner;
import com.gcs.app.rest.TraineeCreateRequest;
import com.gcs.app.rest.TraineeGetResponse;
import com.gcs.app.rest.TraineeTrainingGetResponse;
import com.gcs.app.rest.TraineeUpdateRequest;
import com.gcs.app.rest.TraineeUpdateResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Mock
    private GymFacade gymFacade;

    @InjectMocks
    private TraineeController traineeController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(traineeController).build();
    }

    @Test
    void testRegisterTraineeSuccess() throws Exception {
        TraineeCreateRequest request = new TraineeCreateRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setAddress("123 Main St");

        UserCreationResponse response = new UserCreationResponse();
        response.setUsername("john.doe");
        response.setPassword("secret");

        when(gymFacade.createTrainee(any())).thenReturn(response);

        var result = mockMvc.perform(post(BASE_PATH + "/trainees/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value("secret"));

        verify(gymFacade).createTrainee(any(TraineeCreateRequest.class));
    }

    @Test
    void testGetTraineeProfileSuccess() throws Exception {
        TraineeGetResponse profile = new TraineeGetResponse();
        profile.setUsername("john.doe");
        profile.setFirstName("John");
        profile.setLastName("Doe");

        when(gymFacade.getTraineeByUsername("john.doe")).thenReturn(profile);

        var result = mockMvc.perform(get(BASE_PATH + "/trainees/john.doe"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(gymFacade).getTraineeByUsername("john.doe");
    }

    @Test
    void testUpdateTraineeProfileSuccess() throws Exception {
        TraineeUpdateRequest request = JsonReaderUtil.readFromJson(
                "json/trainee-update-request.json",
                TraineeUpdateRequest.class
        );

        TraineeUpdateResponse response = new TraineeUpdateResponse();
        response.setUsername("john.doe");
        response.setFirstName(request.getFirstName());
        response.setLastName(request.getLastName());
        response.setIsActive(request.getIsActive());

        when(gymFacade.updateTrainee(any(), eq("john.doe"))).thenReturn(response);

        var result = mockMvc.perform(put(BASE_PATH + "/trainees/john.doe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.firstName").value(request.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(request.getLastName()))
                .andExpect(jsonPath("$.isActive").value(request.getIsActive()));

        verify(gymFacade).updateTrainee(any(TraineeUpdateRequest.class), eq("john.doe"));
    }

    @Test
    void testDeleteTraineeProfileSuccess() throws Exception {
        var result = mockMvc.perform(delete(BASE_PATH + "/trainees/john.doe"));

        result.andExpect(status().isOk());

        verify(gymFacade).deleteTraineeByUsername("john.doe");
    }

    @Test
    void testChangeActivationStatusSuccess() throws Exception {
        ActivationStatusRequest request = new ActivationStatusRequest();
        request.setIsActive(false);

        var result = mockMvc.perform(patch(BASE_PATH + "/trainees/john.doe/change-activation-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());

        verify(gymFacade).setTraineeActive("john.doe", false);
    }

    @Test
    void testGetAvailableTrainersSuccess() throws Exception {
        List<AvailableTrainerGetResponse> trainers = JsonReaderUtil.readFromJson(
                "json/available-trainers-response.json",
                new TypeReference<List<AvailableTrainerGetResponse>>() {
                }
        );

        when(gymFacade.getUnassignedTrainers("john.doe")).thenReturn(trainers);

        var result = mockMvc.perform(get(BASE_PATH + "/trainees/john.doe/available-trainers"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("jane.smith"))
                .andExpect(jsonPath("$[0].firstName").value("Jane"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"))
                .andExpect(jsonPath("$[0].specialization").value("Yoga"))
                .andExpect(jsonPath("$[1].username").value("bob.jones"))
                .andExpect(jsonPath("$[1].firstName").value("Bob"))
                .andExpect(jsonPath("$[1].lastName").value("Jones"))
                .andExpect(jsonPath("$[1].specialization").value("Pilates"));

        verify(gymFacade).getUnassignedTrainers("john.doe");
    }

    @Test
    void testUpdateTraineeTrainersSuccess() throws Exception {
        TraineeAssignedTrainersUpdateRequest request = new TraineeAssignedTrainersUpdateRequest();
        request.setTrainerUsernames(List.of("jane.smith", "bob.jones"));

        TraineeAssignedTrainersUpdateResponse response = new TraineeAssignedTrainersUpdateResponse();
        TraineeAssignedTrainersUpdateResponseTrainersInner trainer1 = new TraineeAssignedTrainersUpdateResponseTrainersInner();
        trainer1.setUsername("jane.smith");
        trainer1.setFirstName("Jane");
        trainer1.setLastName("Smith");
        trainer1.setSpecialization("Yoga");
        TraineeAssignedTrainersUpdateResponseTrainersInner trainer2 = new TraineeAssignedTrainersUpdateResponseTrainersInner();
        trainer2.setUsername("bob.jones");
        trainer2.setFirstName("Bob");
        trainer2.setLastName("Jones");
        trainer2.setSpecialization("Pilates");
        response.setTrainers(List.of(trainer1, trainer2));

        when(gymFacade.updateTraineeTrainers(eq("john.doe"), any())).thenReturn(response);

        var result = mockMvc.perform(put(BASE_PATH + "/trainees/john.doe/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.trainers[0].username").value("jane.smith"))
                .andExpect(jsonPath("$.trainers[0].firstName").value("Jane"))
                .andExpect(jsonPath("$.trainers[0].lastName").value("Smith"))
                .andExpect(jsonPath("$.trainers[0].specialization").value("Yoga"))
                .andExpect(jsonPath("$.trainers[1].username").value("bob.jones"))
                .andExpect(jsonPath("$.trainers[1].firstName").value("Bob"))
                .andExpect(jsonPath("$.trainers[1].lastName").value("Jones"))
                .andExpect(jsonPath("$.trainers[1].specialization").value("Pilates"));

        verify(gymFacade).updateTraineeTrainers(eq("john.doe"), any());
    }

    @Test
    void testGetTraineeTrainingsSuccess() throws Exception {
        List<TraineeTrainingGetResponse> trainings = JsonReaderUtil.readFromJson(
                "json/get-trainee-trainings-response.json",
                new TypeReference<List<TraineeTrainingGetResponse>>() {
                }
        );

        when(gymFacade.getTraineeTrainings(any())).thenReturn(trainings);

        var result = mockMvc.perform(get(BASE_PATH + "/trainees/john.doe/trainings")
                .param("periodFrom", "2025-07-01")
                .param("periodTo", "2025-07-31")
                .param("trainerName", "Jane"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Yoga"))
                .andExpect(jsonPath("$[0].trainingDate[0]").value(2025))
                .andExpect(jsonPath("$[0].trainingDate[1]").value(7))
                .andExpect(jsonPath("$[0].trainingDate[2]").value(15))
                .andExpect(jsonPath("$[0].trainerName").value("Jane"))
                .andExpect(jsonPath("$[0].trainingDuration").value(60))
                .andExpect(jsonPath("$[1].trainingName").value("Pilates"))
                .andExpect(jsonPath("$[1].trainingDate[0]").value(2025))
                .andExpect(jsonPath("$[1].trainingDate[1]").value(7))
                .andExpect(jsonPath("$[1].trainingDate[2]").value(20))
                .andExpect(jsonPath("$[1].trainerName").value("Bob"))
                .andExpect(jsonPath("$[1].trainingDuration").value(45));

        verify(gymFacade).getTraineeTrainings(any());
    }

    @Test
    void testGetTraineeTrainingsInvalidDate() throws Exception {
        var result = mockMvc.perform(get(BASE_PATH + "/trainees/john.doe/trainings")
                .param("periodFrom", "invalid-date"));

        result.andExpect(status().isBadRequest());
    }
}