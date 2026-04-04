package com.freelanceos.freelanceappback.application.rest.mapper;

import com.freelanceos.freelanceappback.application.rest.dto.client.ClientRequest;
import com.freelanceos.freelanceappback.application.rest.dto.client.ClientResponse;
import com.freelanceos.freelanceappback.domain.model.client.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapperRest {
    public Client toDomain(ClientRequest request) {
        return new Client(null, null, request.name(), request.contactEmail());
    }

    public Client toDomain(Long id, ClientRequest request) {
        return new Client(id, null, request.name(), request.contactEmail());
    }

    public ClientResponse toResponse(Client client) {
        return new ClientResponse(client.id(), client.name(), client.contactEmail());
    }
}
