package com.freelanceos.freelanceappback.application.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelanceos.freelanceappback.application.rest.dto.user.UserRequest;
import com.freelanceos.freelanceappback.application.rest.mapper.UserMapperRest;
import com.freelanceos.freelanceappback.domain.model.user.User;
import com.freelanceos.freelanceappback.domain.ports.in.user.CreateUserUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.user.DeleteUserUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.user.GetAllUsersUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.user.GetUserByIdUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.user.UpdateUserUseCase;
import com.freelanceos.freelanceappback.infrastructure.security.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserControllerTest.ControllerTestConfig.class)
class UserControllerTest {

    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        UserMapperRest userMapperRest() {
            return new UserMapperRest();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @MockitoBean
    private GetAllUsersUseCase getAllUsersUseCase;

    @MockitoBean
    private GetUserByIdUseCase getUserByIdUseCase;

    @MockitoBean
    private UpdateUserUseCase updateUserUseCase;

    @MockitoBean
    private DeleteUserUseCase deleteUserUseCase;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @Test
    @WithMockUser
    void getUsersShouldReturnList() throws Exception {
        when(getAllUsersUseCase.execute()).thenReturn(List.of(
                new User(1L, "Alice", "alice@example.com"),
                new User(2L, "Bob", "bob@example.com")
        ));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].email").value("alice@example.com"))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @WithMockUser
    void getUserByIdShouldReturnUserWhenFound() throws Exception {
        when(getUserByIdUseCase.execute(1L)).thenReturn(Optional.of(new User(1L, "Alice", "alice@example.com")));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    @WithMockUser
    void getUserByIdShouldReturn404WhenMissing() throws Exception {
        when(getUserByIdUseCase.execute(404L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/404"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createUserShouldReturnCreatedUser() throws Exception {
        when(createUserUseCase.execute(any(User.class))).thenReturn(new User(3L, "Carol", "carol@example.com"));
        String body = objectMapper.writeValueAsString(new UserRequest("Carol", "carol@example.com"));

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Carol"));
    }

    @Test
    @WithMockUser
    void updateUserShouldReturnUpdatedUser() throws Exception {
        when(updateUserUseCase.execute(eq(1L), any(User.class)))
                .thenReturn(Optional.of(new User(1L, "Alice Updated", "alice.updated@example.com")));
        String body = objectMapper.writeValueAsString(new UserRequest("Alice Updated", "alice.updated@example.com"));

        mockMvc.perform(put("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice Updated"));
    }

    @Test
    @WithMockUser
    void deleteUserShouldReturn204WhenDeleted() throws Exception {
        when(deleteUserUseCase.execute(1L)).thenReturn(true);

        mockMvc.perform(delete("/users/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteUserShouldReturn404WhenMissing() throws Exception {
        when(deleteUserUseCase.execute(404L)).thenReturn(false);

        mockMvc.perform(delete("/users/404").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
