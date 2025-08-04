package com.gcs.app.controller;

import com.gcs.app.facade.GymFacade;
import com.gcs.app.rest.TrainingTypeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainingTypeController.class)
@TestPropertySource(properties = {"app.api.base-path=/gym-crm-core/api/v1", "metrics.enabled=false"})
class TrainingTypeControllerTest {

    private static final String TRAINING_NAME_YOGA = "Yoga";
    private static final String TRAINING_NAME_PILATES = "Pilates";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GymFacade gymFacade;

    @Value("${app.api.base-path}")
    private String basePath;

    @Test
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
