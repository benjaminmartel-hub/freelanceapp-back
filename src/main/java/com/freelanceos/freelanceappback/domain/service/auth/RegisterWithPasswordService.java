package com.freelanceos.freelanceappback.domain.service.auth;

import com.freelanceos.freelanceappback.domain.exception.ConflictException;
import com.freelanceos.freelanceappback.domain.model.auth.AuthAccount;
import com.freelanceos.freelanceappback.domain.model.auth.AuthProvider;
import com.freelanceos.freelanceappback.domain.model.user.UserRole;
import com.freelanceos.freelanceappback.domain.ports.in.auth.RegisterWithPasswordUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.domain.ports.out.security.PasswordHasher;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.AuthAccountEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.AuthAccountMapper;
import org.springframework.stereotype.Service;

@Service
public class RegisterWithPasswordService implements RegisterWithPasswordUseCase {
    private final AuthAccountRepository authAccountRepository;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final AuthAccountMapper authAccountMapper;

    public RegisterWithPasswordService(AuthAccountRepository authAccountRepository,
                                       UserRepository userRepository,
                                       PasswordHasher passwordHasher,
                                       AuthAccountMapper authAccountMapper) {
        this.authAccountRepository = authAccountRepository;
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.authAccountMapper = authAccountMapper;
    }

    @Override
    public AuthAccount execute(String username, String password) {
        if (authAccountRepository.findByUsername(username).isPresent()) {
            throw new ConflictException("Username already exists");
        }

        AuthAccount newAccount = new AuthAccount(null, username, passwordHasher.hash(password), AuthProvider.LOCAL, null);
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
            UserEntity userEntity = new UserEntity(null, username, username);
            userEntity.setRoles(java.util.Set.of(UserRole.ROLE_USER));
            userRepository.save(userEntity);
        }
    }
}
