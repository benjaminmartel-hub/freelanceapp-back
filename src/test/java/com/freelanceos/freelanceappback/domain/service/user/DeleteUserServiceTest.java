package com.freelanceos.freelanceappback.domain.service.user;

import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeleteUserService deleteUserService;

    @Test
    void executeShouldReturnTrueWhenDeleted() {
        Long id = 1L;
        when(userRepository.deleteById(id)).thenReturn(true);

        boolean result = deleteUserService.execute(id);

        assertThat(result).isTrue();
        verify(userRepository).deleteById(id);
    }

    @Test
    void executeShouldReturnFalseWhenMissing() {
        Long id = 404L;
        when(userRepository.deleteById(id)).thenReturn(false);

        boolean result = deleteUserService.execute(id);

        assertThat(result).isFalse();
        verify(userRepository).deleteById(id);
    }
}
