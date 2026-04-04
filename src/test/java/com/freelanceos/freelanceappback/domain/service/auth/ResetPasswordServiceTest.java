package com.freelanceos.freelanceappback.domain.service.auth;

import com.freelanceos.freelanceappback.domain.model.auth.AuthProvider;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
import com.freelanceos.freelanceappback.domain.ports.out.security.PasswordHasher;
import com.freelanceos.freelanceappback.domain.exception.BadRequestException;
import com.freelanceos.freelanceappback.domain.exception.NotFoundException;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.AuthAccountEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResetPasswordServiceTest {

    @Mock
    private AuthAccountRepository authAccountRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private ResetPasswordService resetPasswordService;

    @Test
    void executeShouldUpdatePasswordForLocalUser() {
        AuthAccountEntity account = new AuthAccountEntity(1L, "demo", "old-hash", AuthProvider.LOCAL, null);
        when(authAccountRepository.findByUsername("demo")).thenReturn(Optional.of(account));
        when(passwordHasher.hash("newpassword")).thenReturn("new-hash");

        resetPasswordService.execute("demo", "newpassword");

        assertThat(account.getPasswordHash()).isEqualTo("new-hash");
        verify(authAccountRepository).findByUsername("demo");
        verify(passwordHasher).hash("newpassword");
        verify(authAccountRepository).save(account);
        verifyNoMoreInteractions(authAccountRepository, passwordHasher);
    }

    @Test
    void executeShouldThrowWhenPasswordTooShort() {
        assertThatThrownBy(() -> resetPasswordService.execute("demo", "short"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Password must be at least 8 characters");

        verifyNoInteractions(authAccountRepository, passwordHasher);
    }

    @Test
    void executeShouldThrowWhenUserNotFound() {
        when(authAccountRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resetPasswordService.execute("missing", "newpassword"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");

        verify(authAccountRepository).findByUsername("missing");
        verifyNoMoreInteractions(authAccountRepository);
        verifyNoInteractions(passwordHasher);
    }

    @Test
    void executeShouldThrowWhenUserIsNotLocal() {
        AuthAccountEntity account = new AuthAccountEntity(2L, "oauth", "hash", AuthProvider.GOOGLE, "google-123");
        when(authAccountRepository.findByUsername("oauth")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> resetPasswordService.execute("oauth", "newpassword"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("User has no local credentials");

        verify(authAccountRepository).findByUsername("oauth");
        verifyNoMoreInteractions(authAccountRepository);
        verifyNoInteractions(passwordHasher);
    }
}
