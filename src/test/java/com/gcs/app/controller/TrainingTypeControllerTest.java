package com.gcs.app.controller;

import com.gcs.app.rest.TrainingTypeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TrainingTypeControllerTest extends AbstractControllerTest {

    private static final String TRAINING_NAME_YOGA = "Yoga";
    private static final String TRAINING_NAME_PILATES = "Pilates";

    @Test
    @WithMockUser
    void testGetTrainingTypes_returnsListOfTrainingTypes() throws Exception {
        List<TrainingTypeResponse> trainingTypes = List.of(
                new TrainingTypeResponse(TRAINING_NAME_YOGA, 1),
                new TrainingTypeResponse(TRAINING_NAME_PILATES, 2)
        );

        when(gymFacade.getAllTrainingTypes()).thenReturn(trainingTypes);

        mockMvc.perform(get(basePath + "/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingType").value(TRAINING_NAME_YOGA))
                .andExpect(jsonPath("$[0].trainingTypeId").value(1))
                .andExpect(jsonPath("$[1].trainingType").value(TRAINING_NAME_PILATES))
                .andExpect(jsonPath("$[1].trainingTypeId").value(2));
    }
}