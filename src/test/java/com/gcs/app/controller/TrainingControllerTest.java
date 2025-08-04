package com.gcs.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcs.app.facade.GymFacade;
import com.gcs.app.rest.TrainingCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainingController.class)
@TestPropertySource(properties = {"app.api.base-path=/gym-crm-core/api/v1", "metrics.enabled=false"})
class TrainingControllerTest {

    private static final String TRAINEE_USERNAME = "oleksandr.kovalenko";
    private static final String TRAINER_USERNAME = "sofia.melnyk";
    private static final String TRAINING_NAME_YOGA = "Yoga";
    private static final String TRAINING_TYPE_NAME_YOGA = "YOGA";
    private static final LocalDate TRAINING_DATE = LocalDate.ofEpochDay(2025 - 7 - 22);
    private static final int TRAINING_DURATION = 60;

    private final TrainingCreateRequest trainingCreateRequest = createTrainingCreateRequest();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GymFacade gymFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.api.base-path}")
    private String basePath;

    @Test
    void testAddTrainingSuccess() throws Exception {
        mockMvc.perform(post(basePath + "/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingCreateRequest)))
                .andExpect(status().isOk());

        verify(gymFacade).createTraining(any(TrainingCreateRequest.class));
    }

    private TrainingCreateRequest createTrainingCreateRequest() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTrainingName(TRAINING_NAME_YOGA);
        request.setTrainingTypeName(TRAINING_TYPE_NAME_YOGA);
        request.setTraineeUsername(TRAINEE_USERNAME);
        request.setTrainerUsername(TRAINER_USERNAME);
        request.setTrainingDate(TRAINING_DATE);
        request.setTrainingDuration(TRAINING_DURATION);

        return request;
    }
}
