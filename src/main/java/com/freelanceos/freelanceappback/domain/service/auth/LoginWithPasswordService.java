package com.freelanceos.freelanceappback.domain.service.auth;

import com.freelanceos.freelanceappback.domain.exception.UnauthorizedException;
import com.freelanceos.freelanceappback.domain.model.auth.AuthAccount;
import com.freelanceos.freelanceappback.domain.ports.in.auth.LoginWithPasswordUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
import com.freelanceos.freelanceappback.domain.ports.out.security.CredentialsAuthenticator;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.AuthAccountMapper;
import org.springframework.stereotype.Service;

@Service
public class LoginWithPasswordService implements LoginWithPasswordUseCase {
    private final CredentialsAuthenticator credentialsAuthenticator;
    private final AuthAccountRepository authAccountRepository;
    private final AuthAccountMapper authAccountMapper;

    public LoginWithPasswordService(CredentialsAuthenticator credentialsAuthenticator,
                                    AuthAccountRepository authAccountRepository,
                                    AuthAccountMapper authAccountMapper) {
        this.credentialsAuthenticator = credentialsAuthenticator;
        this.authAccountRepository = authAccountRepository;
        this.authAccountMapper = authAccountMapper;
    }

    @Override
    public AuthAccount execute(String username, String password) {
        boolean authenticated = credentialsAuthenticator.authenticate(username, password);
        if (!authenticated) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return authAccountRepository.findByUsername(username)
                .map(authAccountMapper::toDomain)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
    }
}
