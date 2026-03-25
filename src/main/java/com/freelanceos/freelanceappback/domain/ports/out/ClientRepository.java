package com.freelanceos.freelanceappback.domain.ports.out;

import com.freelanceos.freelanceappback.infrastructure.persistence.entity.ClientEntity;

import java.util.Optional;

public interface ClientRepository {
    Optional<ClientEntity> findByIdAndUserId(Long id, Long userId);
}
