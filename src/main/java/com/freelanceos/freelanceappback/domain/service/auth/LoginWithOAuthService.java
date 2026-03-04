package com.freelanceos.freelanceappback.domain.service.auth;

import com.freelanceos.freelanceappback.domain.model.AuthAccount;
import com.freelanceos.freelanceappback.domain.model.AuthProvider;
import com.freelanceos.freelanceappback.domain.ports.in.auth.LoginWithOAuthUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.AuthAccountEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.AuthAccountMapper;
import org.springframework.stereotype.Service;

@Service
public class LoginWithOAuthService implements LoginWithOAuthUseCase {
    private final AuthAccountRepository authAccountRepository;
    private final AuthAccountMapper authAccountMapper;

    public LoginWithOAuthService(AuthAccountRepository authAccountRepository, AuthAccountMapper authAccountMapper) {
        this.authAccountRepository = authAccountRepository;
        this.authAccountMapper = authAccountMapper;
    }

    @Override
    public AuthAccount execute(AuthProvider provider, String providerUserId, String preferredUsername) {
        AuthAccount existingAccount = authAccountRepository.findByProviderAndProviderUserId(provider, providerUserId)
                .map(authAccountMapper::toDomain)
                .orElse(null);

        if (existingAccount != null) {
            return existingAccount;
        }

        String username = preferredUsername;
        if (username == null || username.isBlank()) {
            username = provider.name().toLowerCase() + "_" + providerUserId;
        }

        if (authAccountRepository.findByUsername(username).isPresent()) {
            username = username + "_" + provider.name().toLowerCase();
        }

        AuthAccount newAccount = new AuthAccount(null, username, null, provider, providerUserId);
        AuthAccountEntity savedAccountEntity = authAccountRepository.save(authAccountMapper.toEntity(newAccount));
        return authAccountMapper.toDomain(savedAccountEntity);
    }
}
