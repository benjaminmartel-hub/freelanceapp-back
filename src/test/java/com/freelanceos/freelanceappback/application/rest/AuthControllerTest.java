package com.freelanceos.freelanceappback.application.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelanceos.freelanceappback.application.rest.dto.auth.AuthResponse;
import com.freelanceos.freelanceappback.application.rest.dto.auth.LoginRequest;
import com.freelanceos.freelanceappback.application.rest.dto.auth.RegisterRequest;
import com.freelanceos.freelanceappback.application.rest.mapper.AuthMapperRest;
import com.freelanceos.freelanceappback.domain.model.auth.AuthAccount;
import com.freelanceos.freelanceappback.domain.model.auth.AuthProvider;
import com.freelanceos.freelanceappback.domain.ports.in.auth.GetCurrentAuthenticatedUserUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.auth.LoginWithPasswordUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.auth.RegisterWithPasswordUseCase;
import com.freelanceos.freelanceappback.infrastructure.security.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RegisterWithPasswordUseCase registerWithPasswordUseCase;

    @MockitoBean
    private LoginWithPasswordUseCase loginWithPasswordUseCase;

    @MockitoBean
    private GetCurrentAuthenticatedUserUseCase getCurrentAuthenticatedUserUseCase;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @MockitoBean
    private AuthMapperRest authMapperRest;

    @Test
    void registerShouldReturnCreatedResponse() throws Exception {
        when(registerWithPasswordUseCase.execute("alice", "secret"))
                .thenReturn(new AuthAccount("alice", AuthProvider.LOCAL));
        when(jwtTokenService.generateToken("alice", "LOCAL")).thenReturn("fake-jwt-token");
        when(authMapperRest.toResponse("fake-jwt-token")).thenReturn(new AuthResponse("fake-jwt-token"));

        String body = objectMapper.writeValueAsString(new RegisterRequest("alice", "secret"));

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void loginShouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        when(loginWithPasswordUseCase.execute("alice", "bad"))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        String body = objectMapper.writeValueAsString(new LoginRequest("alice", "bad"));

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meShouldReturnAuthenticatedUser() throws Exception {
        when(getCurrentAuthenticatedUserUseCase.execute("alice"))
                .thenReturn(new AuthAccount("alice", AuthProvider.LOCAL));
        when(jwtTokenService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtTokenService.extractUsername("valid-token")).thenReturn("alice");

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.provider").value("LOCAL"));
    }

    @Test
    void meShouldReturnUnauthorizedWhenMissingAuthentication() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
