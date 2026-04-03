package com.freelanceos.freelanceappback.domain.service.auth;

import com.freelanceos.freelanceappback.domain.model.auth.AuthAccount;
import com.freelanceos.freelanceappback.domain.model.auth.AuthProvider;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
import com.freelanceos.freelanceappback.domain.ports.out.security.CredentialsAuthenticator;
import com.freelanceos.freelanceappback.domain.exception.UnauthorizedException;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.AuthAccountEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.AuthAccountMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginWithPasswordServiceTest {

    @Mock
    private CredentialsAuthenticator credentialsAuthenticator;

    @Mock
    private AuthAccountRepository authAccountRepository;

    @Mock
    private AuthAccountMapper authAccountMapper;

    @InjectMocks
    private LoginWithPasswordService loginWithPasswordService;

    @Test
    void executeShouldReturnAuthAccountWhenCredentialsAreValid() {
        when(credentialsAuthenticator.authenticate("alice", "secret")).thenReturn(true);
        when(authAccountRepository.findByUsername("alice"))
                .thenReturn(Optional.of(new AuthAccountEntity(1L, "alice", "hash", AuthProvider.LOCAL, null)));
        when(authAccountMapper.toDomain(org.mockito.ArgumentMatchers.any(AuthAccountEntity.class)))
                .thenReturn(new AuthAccount(1L, "alice", "hash", AuthProvider.LOCAL, null));

        AuthAccount result = loginWithPasswordService.execute("alice", "secret");

        assertThat(result.username()).isEqualTo("alice");
        assertThat(result.provider()).isEqualTo(AuthProvider.LOCAL);
    }

    @Test
    void executeShouldThrowWhenCredentialsAreInvalid() {
        when(credentialsAuthenticator.authenticate("alice", "bad")).thenReturn(false);

        assertThatThrownBy(() -> loginWithPasswordService.execute("alice", "bad"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid credentials");
    }
}
