package com.freelanceos.freelanceappback.domain.ports.in.client;

import com.freelanceos.freelanceappback.domain.model.client.Client;

public interface CreateClientUseCase {
    Client execute(String username, Client clientToCreate);
}
