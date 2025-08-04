package com.gcs.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcs.app.facade.GymFacade;
import com.gcs.app.facade.dto.AuthResponseDto;
import com.gcs.app.rest.ChangePasswordRequest;
import com.gcs.app.rest.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@TestPropertySource(properties = {"app.api.base-path=/gym-crm-core/api/v1", "metrics.enabled=false"})
class AuthControllerTest {

    private static final String USERNAME = "ivan.ivanov";
    private static final String PASSWORD = "password123";
    private static final String NEW_PASSWORD = "newPassword456";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GymFacade gymFacade;

    @Value("${app.api.base-path}")
    private String basePath;

    @Test
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);

        when(gymFacade.authenticate(any(LoginRequest.class)))
                .thenReturn(new AuthResponseDto());

        var result = mockMvc.perform(post(basePath + "/login")
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());

        verify(gymFacade).changePassword(any(ChangePasswordRequest.class));
    }
}
