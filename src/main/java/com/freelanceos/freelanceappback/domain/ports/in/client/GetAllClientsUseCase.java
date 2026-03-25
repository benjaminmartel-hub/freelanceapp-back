package com.freelanceos.freelanceappback.domain.ports.in.client;

import com.freelanceos.freelanceappback.domain.model.client.Client;

import java.util.List;

public interface GetAllClientsUseCase {
    List<Client> execute(String username);
}
