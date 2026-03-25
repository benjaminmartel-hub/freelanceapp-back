package com.freelanceos.freelanceappback.application.rest;

import com.freelanceos.freelanceappback.application.rest.dto.client.ClientRequest;
import com.freelanceos.freelanceappback.application.rest.dto.client.ClientResponse;
import com.freelanceos.freelanceappback.application.rest.mapper.ClientMapperRest;
import com.freelanceos.freelanceappback.domain.model.client.Client;
import com.freelanceos.freelanceappback.domain.ports.in.client.CreateClientUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.client.DeleteClientUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.client.GetAllClientsUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.client.GetClientByIdUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.client.UpdateClientUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final CreateClientUseCase createClientUseCase;
    private final GetAllClientsUseCase getAllClientsUseCase;
    private final GetClientByIdUseCase getClientByIdUseCase;
    private final UpdateClientUseCase updateClientUseCase;
    private final DeleteClientUseCase deleteClientUseCase;
    private final ClientMapperRest clientMapperRest;

    public ClientController(CreateClientUseCase createClientUseCase,
                            GetAllClientsUseCase getAllClientsUseCase,
                            GetClientByIdUseCase getClientByIdUseCase,
                            UpdateClientUseCase updateClientUseCase,
                            DeleteClientUseCase deleteClientUseCase,
                            ClientMapperRest clientMapperRest) {
        this.createClientUseCase = createClientUseCase;
        this.getAllClientsUseCase = getAllClientsUseCase;
        this.getClientByIdUseCase = getClientByIdUseCase;
        this.updateClientUseCase = updateClientUseCase;
        this.deleteClientUseCase = deleteClientUseCase;
        this.clientMapperRest = clientMapperRest;
    }

    @GetMapping
    public List<ClientResponse> getClients(Principal principal) {
        String username = resolveUsername(principal);
        return getAllClientsUseCase.execute(username).stream()
                .map(clientMapperRest::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ClientResponse getClientById(@PathVariable Long id, Principal principal) {
        String username = resolveUsername(principal);
        return getClientByIdUseCase.execute(username, id)
                .map(clientMapperRest::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse createClient(@Valid @RequestBody ClientRequest request, Principal principal) {
        String username = resolveUsername(principal);
        try {
            Client clientToCreate = clientMapperRest.toDomain(request);
            return clientMapperRest.toResponse(createClientUseCase.execute(username, clientToCreate));
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ClientResponse updateClient(@PathVariable Long id,
                                       @Valid @RequestBody ClientRequest request,
                                       Principal principal) {
        String username = resolveUsername(principal);
        try {
            Client clientToUpdate = clientMapperRest.toDomain(id, request);
            return updateClientUseCase.execute(username, id, clientToUpdate)
                    .map(clientMapperRest::toResponse)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long id, Principal principal) {
        String username = resolveUsername(principal);
        boolean deleted = deleteClientUseCase.delete(username, id);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
    }

    private String resolveUsername(Principal principal) {
        String username = principal != null ? principal.getName() : null;
        if (username == null || username.isBlank()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                username = authentication.getName();
            }
        }

        if (username == null || username.isBlank() || "anonymousUser".equals(username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        return username;
    }
}
