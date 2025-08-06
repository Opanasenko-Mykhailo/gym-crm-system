package com.gcs.app.controller.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TrainingTypeSecurityControllerTest extends AbstractSecurityControllerTest {

    @Test
    @WithAnonymousUser
    void getTrainingTypes_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get(basePath + "/training-types"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getTrainingTypes_shouldReturnOkForAuthenticatedUser() throws Exception {
        mockMvc.perform(get(basePath + "/training-types"))
                .andExpect(status().isOk());
    }
}