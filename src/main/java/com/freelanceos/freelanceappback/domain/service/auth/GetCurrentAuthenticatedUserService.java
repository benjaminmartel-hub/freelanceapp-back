package com.freelanceos.freelanceappback.domain.service.auth;

import com.freelanceos.freelanceappback.domain.model.AuthAccount;
import com.freelanceos.freelanceappback.domain.ports.in.auth.GetCurrentAuthenticatedUserUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.AuthAccountMapper;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentAuthenticatedUserService implements GetCurrentAuthenticatedUserUseCase {
    private final AuthAccountRepository authAccountRepository;
    private final AuthAccountMapper authAccountMapper;

    public GetCurrentAuthenticatedUserService(AuthAccountRepository authAccountRepository,
                                              AuthAccountMapper authAccountMapper) {
        this.authAccountRepository = authAccountRepository;
        this.authAccountMapper = authAccountMapper;
    }

    @Override
    public AuthAccount execute(String username) {
        return authAccountRepository.findByUsername(username)
                .map(authAccountMapper::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
    }
}
