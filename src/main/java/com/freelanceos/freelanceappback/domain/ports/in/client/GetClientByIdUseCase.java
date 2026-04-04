package com.freelanceos.freelanceappback.domain.ports.in.client;

import com.freelanceos.freelanceappback.domain.model.client.Client;

import java.util.Optional;

public interface GetClientByIdUseCase {
    Optional<Client> execute(String username, Long id);
}
