package com.freelanceos.freelanceappback.infrastructure.persistence.repository;

import com.freelanceos.freelanceappback.infrastructure.persistence.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataClientJpaRepository extends JpaRepository<ClientEntity, Long> {
    Optional<ClientEntity> findByIdAndUserId(Long id, Long userId);
}
