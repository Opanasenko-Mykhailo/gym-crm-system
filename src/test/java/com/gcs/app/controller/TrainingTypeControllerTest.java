package com.gcs.app.controller;

import com.gcs.app.facade.GymFacade;
import com.gcs.app.rest.TrainingTypeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.gcs.app.controller.ApiConstant.BASE_PATH;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainingTypeControllerTest {

    private static final String TRAINING_NAME_YOGA = "Yoga";
    private static final String TRAINING_NAME_PILATES = "Pilates";

    private MockMvc mockMvc;

    @Mock
    private GymFacade gymFacade;

    @InjectMocks
    private TrainingTypeController trainingTypeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainingTypeController).build();
    }

    @Test
    void testGetTrainingTypes_returnsListOfTrainingTypes() throws Exception {
        List<TrainingTypeResponse> trainingTypes = List.of(
                new TrainingTypeResponse(TRAINING_NAME_YOGA, 1),
                new TrainingTypeResponse(TRAINING_NAME_PILATES, 2)
        );

        when(gymFacade.getAllTrainingTypes()).thenReturn(trainingTypes);

        mockMvc.perform(get(BASE_PATH + "/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingType").value(TRAINING_NAME_YOGA))
                .andExpect(jsonPath("$[0].trainingTypeId").value(1))
                .andExpect(jsonPath("$[1].trainingType").value(TRAINING_NAME_PILATES))
                .andExpect(jsonPath("$[1].trainingTypeId").value(2));
    }
}
