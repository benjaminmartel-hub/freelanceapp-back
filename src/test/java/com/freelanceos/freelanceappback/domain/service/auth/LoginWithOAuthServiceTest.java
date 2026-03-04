package com.freelanceos.freelanceappback.domain.service.auth;

import com.freelanceos.freelanceappback.domain.model.AuthAccount;
import com.freelanceos.freelanceappback.domain.model.AuthProvider;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.AuthAccountEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.AuthAccountMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginWithOAuthServiceTest {

    @Mock
    private AuthAccountRepository authAccountRepository;

    @Mock
    private AuthAccountMapper authAccountMapper;

    @InjectMocks
    private LoginWithOAuthService loginWithOAuthService;

    @Test
    void executeShouldReturnExistingOAuthAccountWhenPresent() {
        when(authAccountRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, "oauth-id"))
                .thenReturn(Optional.of(new AuthAccountEntity(1L, "alice@example.com", null, AuthProvider.GOOGLE, "oauth-id")));
        when(authAccountMapper.toDomain(any(AuthAccountEntity.class)))
                .thenReturn(new AuthAccount(1L, "alice@example.com", null, AuthProvider.GOOGLE, "oauth-id"));

        AuthAccount result = loginWithOAuthService.execute(AuthProvider.GOOGLE, "oauth-id", "alice@example.com");

        assertThat(result.getUsername()).isEqualTo("alice@example.com");
        assertThat(result.getProvider()).isEqualTo(AuthProvider.GOOGLE);
    }

    @Test
    void executeShouldCreateOAuthAccountWhenMissing() {
        when(authAccountRepository.findByProviderAndProviderUserId(AuthProvider.GITHUB, "gh-123"))
                .thenReturn(Optional.empty());
        when(authAccountRepository.findByUsername("octocat")).thenReturn(Optional.empty());
        when(authAccountMapper.toEntity(any(AuthAccount.class)))
                .thenReturn(new AuthAccountEntity(null, "octocat", null, AuthProvider.GITHUB, "gh-123"));
        when(authAccountRepository.save(any(AuthAccountEntity.class)))
                .thenReturn(new AuthAccountEntity(10L, "octocat", null, AuthProvider.GITHUB, "gh-123"));
        when(authAccountMapper.toDomain(any(AuthAccountEntity.class)))
                .thenReturn(new AuthAccount(10L, "octocat", null, AuthProvider.GITHUB, "gh-123"));

        AuthAccount result = loginWithOAuthService.execute(AuthProvider.GITHUB, "gh-123", "octocat");

        assertThat(result.getUsername()).isEqualTo("octocat");
        assertThat(result.getProvider()).isEqualTo(AuthProvider.GITHUB);
    }
}
