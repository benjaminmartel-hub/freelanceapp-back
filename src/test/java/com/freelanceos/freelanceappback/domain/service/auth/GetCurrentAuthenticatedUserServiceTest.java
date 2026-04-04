package com.freelanceos.freelanceappback.domain.service.auth;

import com.freelanceos.freelanceappback.domain.model.auth.AuthAccount;
import com.freelanceos.freelanceappback.domain.model.auth.AuthProvider;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrentAuthenticatedUserServiceTest {

    @Mock
    private AuthAccountRepository authAccountRepository;

    @Mock
    private AuthAccountMapper authAccountMapper;

    @InjectMocks
    private GetCurrentAuthenticatedUserService getCurrentAuthenticatedUserService;

    @Test
    void executeShouldReturnAuthenticatedUserWhenAccountExists() {
        when(authAccountRepository.findByUsername("alice"))
                .thenReturn(Optional.of(new AuthAccountEntity(1L, "alice", "hash", AuthProvider.LOCAL, null)));
        when(authAccountMapper.toDomain(any(AuthAccountEntity.class)))
                .thenReturn(new AuthAccount(1L, "alice", "hash", AuthProvider.LOCAL, null));

        AuthAccount result = getCurrentAuthenticatedUserService.execute("alice");

        assertThat(result.username()).isEqualTo("alice");
        assertThat(result.provider()).isEqualTo(AuthProvider.LOCAL);
    }

    @Test
    void executeShouldThrowWhenAccountDoesNotExist() {
        when(authAccountRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getCurrentAuthenticatedUserService.execute("ghost"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Authenticated user not found");
    }
}
