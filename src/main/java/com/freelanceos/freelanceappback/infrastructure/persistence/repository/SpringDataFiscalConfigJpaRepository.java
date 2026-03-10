package com.freelanceos.freelanceappback.infrastructure.persistence.repository;

import com.freelanceos.freelanceappback.infrastructure.persistence.entity.FiscalConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataFiscalConfigJpaRepository extends JpaRepository<FiscalConfigEntity, Long> {
    Optional<FiscalConfigEntity> findByUserId(Long userId);
}
