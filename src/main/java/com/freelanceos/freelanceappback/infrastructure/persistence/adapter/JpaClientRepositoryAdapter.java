package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.ports.out.ClientRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.ClientEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.repository.SpringDataClientJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaClientRepositoryAdapter implements ClientRepository {
    private final SpringDataClientJpaRepository clientJpaRepository;

    public JpaClientRepositoryAdapter(SpringDataClientJpaRepository clientJpaRepository) {
        this.clientJpaRepository = clientJpaRepository;
    }

    @Override
    public Optional<ClientEntity> findByIdAndUserId(Long id, Long userId) {
        return clientJpaRepository.findByIdAndUserId(id, userId);
    }
}
