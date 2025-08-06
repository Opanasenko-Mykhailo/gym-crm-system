package com.gcs.app.controller;

import com.gcs.app.rest.ChangePasswordRequest;
import com.gcs.app.rest.LoginRequest;
import com.gcs.app.rest.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends AbstractControllerTest {

    private static final String USERNAME = "ivan.ivanov";
    private static final String PASSWORD = "Password!123";
    private static final String NEW_PASSWORD = "newPassword!456";

    @Test
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);

        when(gymFacade.authenticate(any(LoginRequest.class)))
                .thenReturn(new LoginResponse());

        var result = mockMvc.perform(post(basePath + "/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());

        verify(gymFacade).authenticate(any(LoginRequest.class));
    }

    @Test
    void testChangePasswordSuccess() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername(USERNAME);
        request.setOldPassword(PASSWORD);
        request.setNewPassword(NEW_PASSWORD);

        doNothing().when(gymFacade).changePassword(any(ChangePasswordRequest.class));

        var result = mockMvc.perform(put(basePath + "/change-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());

        verify(gymFacade).changePassword(any(ChangePasswordRequest.class));
    }
}