package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.model.auth.AuthProvider;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.AuthAccountEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.repository.SpringDataAuthAccountJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaAuthAccountRepositoryAdapter implements AuthAccountRepository {
    private final SpringDataAuthAccountJpaRepository authAccountJpaRepository;

    public JpaAuthAccountRepositoryAdapter(SpringDataAuthAccountJpaRepository authAccountJpaRepository) {
        this.authAccountJpaRepository = authAccountJpaRepository;
    }

    @Override
    public Optional<AuthAccountEntity> findByUsername(String username) {
        return authAccountJpaRepository.findByUsername(username);
    }

    @Override
    public Optional<AuthAccountEntity> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId) {
        return authAccountJpaRepository.findByProviderAndProviderUserId(provider, providerUserId);
    }

    @Override
    public AuthAccountEntity save(AuthAccountEntity authAccount) {
        return authAccountJpaRepository.save(authAccount);
    }
}
