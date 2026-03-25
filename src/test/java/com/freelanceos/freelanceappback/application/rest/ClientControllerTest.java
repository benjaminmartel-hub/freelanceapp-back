package com.freelanceos.freelanceappback.application.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelanceos.freelanceappback.application.rest.dto.client.ClientRequest;
import com.freelanceos.freelanceappback.application.rest.mapper.ClientMapperRest;
import com.freelanceos.freelanceappback.domain.model.client.Client;
import com.freelanceos.freelanceappback.domain.ports.in.client.CreateClientUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.client.DeleteClientUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.client.GetAllClientsUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.client.GetClientByIdUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.client.UpdateClientUseCase;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@Import(ClientControllerTest.ControllerTestConfig.class)
class ClientControllerTest {

    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        ClientMapperRest clientMapperRest() {
            return new ClientMapperRest();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CreateClientUseCase createClientUseCase;

    @MockitoBean
    private GetAllClientsUseCase getAllClientsUseCase;

    @MockitoBean
    private GetClientByIdUseCase getClientByIdUseCase;

    @MockitoBean
    private UpdateClientUseCase updateClientUseCase;

    @MockitoBean
    private DeleteClientUseCase deleteClientUseCase;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @Test
    @WithMockUser(username = "demo")
    void getClientsShouldReturnList() throws Exception {
        when(getAllClientsUseCase.execute("demo")).thenReturn(List.of(
                new Client(1L, 1L, "Maison Beldi", "contact@maisonbeldi.com"),
                new Client(2L, 1L, "Alpha", "hello@alpha.com")
        ));

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Maison Beldi"))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @WithMockUser(username = "demo")
    void getClientByIdShouldReturnClientWhenFound() throws Exception {
        when(getClientByIdUseCase.execute("demo", 1L))
                .thenReturn(Optional.of(new Client(1L, 1L, "Maison Beldi", "contact@maisonbeldi.com")));

        mockMvc.perform(get("/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Maison Beldi"));
    }

    @Test
    @WithMockUser(username = "demo")
    void getClientByIdShouldReturn404WhenMissing() throws Exception {
        when(getClientByIdUseCase.execute("demo", 404L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/clients/404"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "demo")
    void createClientShouldReturnCreatedClient() throws Exception {
        when(createClientUseCase.execute(eq("demo"), any(Client.class)))
                .thenReturn(new Client(3L, 1L, "Gamma", "hi@gamma.com"));

        String body = objectMapper.writeValueAsString(new ClientRequest("Gamma", "hi@gamma.com"));

        mockMvc.perform(post("/clients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Gamma"));
    }

    @Test
    @WithMockUser(username = "demo")
    void updateClientShouldReturnUpdatedClient() throws Exception {
        when(updateClientUseCase.execute(eq("demo"), eq(1L), any(Client.class)))
                .thenReturn(Optional.of(new Client(1L, 1L, "Maison Beldi Updated", "contact@maisonbeldi.com")));

        String body = objectMapper.writeValueAsString(new ClientRequest("Maison Beldi Updated", "contact@maisonbeldi.com"));

        mockMvc.perform(put("/clients/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Maison Beldi Updated"));
    }

    @Test
    @WithMockUser(username = "demo")
    void deleteClientShouldReturn204WhenDeleted() throws Exception {
        when(deleteClientUseCase.delete("demo", 1L)).thenReturn(true);

        mockMvc.perform(delete("/clients/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "demo")
    void deleteClientShouldReturn404WhenMissing() throws Exception {
        when(deleteClientUseCase.delete("demo", 404L)).thenReturn(false);

        mockMvc.perform(delete("/clients/404").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
