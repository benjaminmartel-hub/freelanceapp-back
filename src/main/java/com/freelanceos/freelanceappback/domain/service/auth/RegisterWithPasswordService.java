package com.freelanceos.freelanceappback.domain.service.auth;

import com.freelanceos.freelanceappback.domain.model.AuthAccount;
import com.freelanceos.freelanceappback.domain.model.AuthProvider;
import com.freelanceos.freelanceappback.domain.ports.in.auth.RegisterWithPasswordUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
import com.freelanceos.freelanceappback.domain.ports.out.security.PasswordHasher;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.AuthAccountEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.AuthAccountMapper;
import org.springframework.stereotype.Service;

@Service
public class RegisterWithPasswordService implements RegisterWithPasswordUseCase {
    private final AuthAccountRepository authAccountRepository;
    private final PasswordHasher passwordHasher;
    private final AuthAccountMapper authAccountMapper;

    public RegisterWithPasswordService(AuthAccountRepository authAccountRepository,
                                       PasswordHasher passwordHasher,
                                       AuthAccountMapper authAccountMapper) {
        this.authAccountRepository = authAccountRepository;
        this.passwordHasher = passwordHasher;
        this.authAccountMapper = authAccountMapper;
    }

    @Override
    public AuthAccount execute(String username, String password) {
        if (authAccountRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }

        AuthAccount newAccount = new AuthAccount(null, username, passwordHasher.hash(password), AuthProvider.LOCAL, null);
        AuthAccountEntity savedAccountEntity = authAccountRepository.save(authAccountMapper.toEntity(newAccount));
        return authAccountMapper.toDomain(savedAccountEntity);
    }
}
