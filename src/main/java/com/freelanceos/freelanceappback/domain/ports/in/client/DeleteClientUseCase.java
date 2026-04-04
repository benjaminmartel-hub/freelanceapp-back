package com.freelanceos.freelanceappback.domain.ports.in.client;

public interface DeleteClientUseCase {
    boolean delete(String username, Long id);
}
