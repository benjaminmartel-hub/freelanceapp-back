package com.freelanceos.freelanceappback.domain.service.auth;

import com.freelanceos.freelanceappback.domain.model.auth.AuthAccount;
import com.freelanceos.freelanceappback.domain.model.auth.AuthProvider;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
import com.freelanceos.freelanceappback.domain.ports.out.security.PasswordHasher;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterWithPasswordServiceTest {

    @Mock
    private AuthAccountRepository authAccountRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private AuthAccountMapper authAccountMapper;

    @InjectMocks
    private RegisterWithPasswordService registerWithPasswordService;

    @Test
    void executeShouldCreateLocalAccountWhenUsernameDoesNotExist() {
        when(authAccountRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(passwordHasher.hash("secret")).thenReturn("hashed-secret");
        when(authAccountMapper.toEntity(any(AuthAccount.class)))
                .thenReturn(new AuthAccountEntity(null, "alice", "hashed-secret", AuthProvider.LOCAL, null));
        when(authAccountRepository.save(any(AuthAccountEntity.class)))
                .thenReturn(new AuthAccountEntity(1L, "alice", "hashed-secret", AuthProvider.LOCAL, null));
        when(authAccountMapper.toDomain(any(AuthAccountEntity.class)))
                .thenReturn(new AuthAccount(1L, "alice", "hashed-secret", AuthProvider.LOCAL, null));

        AuthAccount result = registerWithPasswordService.execute("alice", "secret");

        assertThat(result.username()).isEqualTo("alice");
        assertThat(result.provider()).isEqualTo(AuthProvider.LOCAL);
        verify(authAccountRepository).findByUsername("alice");
        verify(passwordHasher).hash("secret");
    }

    @Test
    void executeShouldThrowWhenUsernameAlreadyExists() {
        when(authAccountRepository.findByUsername("alice"))
                .thenReturn(Optional.of(new AuthAccountEntity(1L, "alice", "hash", AuthProvider.LOCAL, null)));

        assertThatThrownBy(() -> registerWithPasswordService.execute("alice", "secret"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Username already exists");
    }
}
