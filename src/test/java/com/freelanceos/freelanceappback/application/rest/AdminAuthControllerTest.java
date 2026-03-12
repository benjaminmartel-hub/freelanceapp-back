package com.freelanceos.freelanceappback.application.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelanceos.freelanceappback.application.rest.dto.auth.ResetPasswordRequest;
import com.freelanceos.freelanceappback.domain.ports.in.auth.ResetPasswordUseCase;
import com.freelanceos.freelanceappback.infrastructure.security.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAuthController.class)
class AdminAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ResetPasswordUseCase resetPasswordUseCase;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @Test
    @WithMockUser
    void resetPasswordShouldReturnNoContent() throws Exception {
        String body = objectMapper.writeValueAsString(new ResetPasswordRequest("demo", "newpassword"));

        mockMvc.perform(post("/admin/auth/reset-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void resetPasswordShouldReturnNotFoundWhenUserMissing() throws Exception {
        doThrow(new IllegalArgumentException("User not found"))
                .when(resetPasswordUseCase).execute("missing", "newpassword");

        String body = objectMapper.writeValueAsString(new ResetPasswordRequest("missing", "newpassword"));

        mockMvc.perform(post("/admin/auth/reset-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void resetPasswordShouldReturnBadRequestWhenInvalid() throws Exception {
        doThrow(new IllegalArgumentException("Password must be at least 8 characters"))
                .when(resetPasswordUseCase).execute("demo", "short");

        String body = objectMapper.writeValueAsString(new ResetPasswordRequest("demo", "short"));

        mockMvc.perform(post("/admin/auth/reset-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
