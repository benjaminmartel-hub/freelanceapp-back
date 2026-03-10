package com.freelanceos.freelanceappback.domain.ports.out;

import com.freelanceos.freelanceappback.domain.model.auth.AuthProvider;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.AuthAccountEntity;

import java.util.Optional;

public interface AuthAccountRepository {
    Optional<AuthAccountEntity> findByUsername(String username);

    Optional<AuthAccountEntity> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

    AuthAccountEntity save(AuthAccountEntity authAccount);
}
