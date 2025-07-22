package com.gcs.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gcs.app.facade.GymFacade;
import com.gcs.app.rest.TrainingCreateRequest;
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

import static com.gcs.app.controller.ApiConstant.BASE_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    private static final String TRAINEE_USERNAME = "oleksandr.kovalenko";
    private static final String TRAINER_USERNAME = "sofia.melnyk";
    private static final String TRAINING_NAME_YOGA = "Yoga";
    private static final LocalDate TRAINING_DATE = LocalDate.ofEpochDay(2025 - 7 - 22);
    private static final int TRAINING_DURATION = 60;

    private final TrainingCreateRequest trainingCreateRequest = createTrainingCreateRequest();

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Mock
    private GymFacade gymFacade;

    @InjectMocks
    private TrainingController trainingController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(trainingController).build();
    }

    @Test
    void testAddTrainingSuccess() throws Exception {
        mockMvc.perform(post(BASE_PATH + "/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingCreateRequest)))
                .andExpect(status().isOk());

        verify(gymFacade).createTraining(any(TrainingCreateRequest.class));
    }

    private TrainingCreateRequest createTrainingCreateRequest() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTrainingName(TRAINING_NAME_YOGA);
        request.setTraineeUsername(TRAINEE_USERNAME);
        request.setTrainerUsername(TRAINER_USERNAME);
        request.setTrainingDate(TRAINING_DATE);
        request.setTrainingDuration(TRAINING_DURATION);

        return request;
    }
}
