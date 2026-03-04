package com.freelanceos.freelanceappback.infrastructure.persistence.repository;

import com.freelanceos.freelanceappback.domain.model.AuthProvider;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.AuthAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataAuthAccountJpaRepository extends JpaRepository<AuthAccountEntity, Long> {
    Optional<AuthAccountEntity> findByUsername(String username);

    Optional<AuthAccountEntity> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
}
