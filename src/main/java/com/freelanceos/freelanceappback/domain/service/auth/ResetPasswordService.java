package com.freelanceos.freelanceappback.domain.service.auth;

import com.freelanceos.freelanceappback.domain.model.auth.AuthProvider;
import com.freelanceos.freelanceappback.domain.ports.in.auth.ResetPasswordUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
import com.freelanceos.freelanceappback.domain.ports.out.security.PasswordHasher;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.AuthAccountEntity;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordService implements ResetPasswordUseCase {
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final AuthAccountRepository authAccountRepository;
    private final PasswordHasher passwordHasher;

    public ResetPasswordService(AuthAccountRepository authAccountRepository,
                                PasswordHasher passwordHasher) {
        this.authAccountRepository = authAccountRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void execute(String username, String newPassword) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }

        AuthAccountEntity account = authAccountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (account.getProvider() != AuthProvider.LOCAL) {
            throw new IllegalArgumentException("User has no local credentials");
        }

        account.setPasswordHash(passwordHasher.hash(newPassword));
        authAccountRepository.save(account);
    }
}
