package com.freelanceos.freelanceappback.infrastructure.persistence.repository;

import com.freelanceos.freelanceappback.infrastructure.persistence.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataClientJpaRepository extends JpaRepository<ClientEntity, Long> {
    List<ClientEntity> findByUserIdOrderByNameAsc(Long userId);

    Optional<ClientEntity> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);

    boolean existsByUserIdAndNameIgnoreCaseAndIdNot(Long userId, String name, Long id);

    long deleteByIdAndUserId(Long id, Long userId);
}
