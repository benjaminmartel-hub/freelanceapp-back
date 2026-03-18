package com.freelanceos.freelanceappback.domain.service.auth;

import com.freelanceos.freelanceappback.domain.model.auth.AuthAccount;
import com.freelanceos.freelanceappback.domain.model.auth.AuthProvider;
import com.freelanceos.freelanceappback.domain.ports.in.auth.LoginWithOAuthUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.AuthAccountEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.AuthAccountMapper;
import org.springframework.stereotype.Service;

@Service
public class LoginWithOAuthService implements LoginWithOAuthUseCase {
    private final AuthAccountRepository authAccountRepository;
    private final UserRepository userRepository;
    private final AuthAccountMapper authAccountMapper;

    public LoginWithOAuthService(AuthAccountRepository authAccountRepository,
                                 UserRepository userRepository,
                                 AuthAccountMapper authAccountMapper) {
        this.authAccountRepository = authAccountRepository;
        this.userRepository = userRepository;
        this.authAccountMapper = authAccountMapper;
    }

    @Override
    public AuthAccount execute(AuthProvider provider, String providerUserId, String preferredUsername) {
        AuthAccountEntity existingAccount = authAccountRepository.findByProviderAndProviderUserId(provider, providerUserId)
                .orElse(null);

        if (existingAccount != null) {
            ensureUserExists(existingAccount.getUsername());
            return authAccountMapper.toDomain(existingAccount);
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
        ensureUserExists(savedAccountEntity.getUsername());
        return authAccountMapper.toDomain(savedAccountEntity);
    }

    private void ensureUserExists(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        boolean exists = userRepository.findByEmailIgnoreCase(username).isPresent()
                || userRepository.findByNameIgnoreCase(username).isPresent();
        if (!exists) {
            userRepository.save(new UserEntity(null, username, username));
        }
    }
}
