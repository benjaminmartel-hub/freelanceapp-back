package com.freelanceos.freelanceappback.domain.ports.out;

import com.freelanceos.freelanceappback.infrastructure.persistence.entity.ClientEntity;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    List<ClientEntity> findByUserId(Long userId);

    Optional<ClientEntity> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);

    boolean existsByUserIdAndNameIgnoreCaseAndIdNot(Long userId, String name, Long id);

    ClientEntity save(ClientEntity client);

    Optional<ClientEntity> update(Long id, ClientEntity client);

    boolean delete(Long id, Long userId);
}
