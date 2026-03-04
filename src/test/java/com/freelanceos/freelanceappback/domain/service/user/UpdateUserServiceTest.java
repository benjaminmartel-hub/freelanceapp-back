package com.freelanceos.freelanceappback.domain.service.user;

import com.freelanceos.freelanceappback.domain.model.User;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UpdateUserService updateUserService;

    @Test
    void executeShouldReturnMappedUserWhenUpdated() {
        Long id = 1L;
        User inputUser = new User(id, "Alice Updated", "alice.updated@example.com");
        UserEntity inputEntity = new UserEntity(id, "Alice Updated", "alice.updated@example.com");
        UserEntity updatedEntity = new UserEntity(id, "Alice Updated", "alice.updated@example.com");
        User updatedUser = new User(id, "Alice Updated", "alice.updated@example.com");

        when(userMapper.toEntity(inputUser)).thenReturn(inputEntity);
        when(userRepository.update(id, inputEntity)).thenReturn(Optional.of(updatedEntity));
        when(userMapper.toDomain(updatedEntity)).thenReturn(updatedUser);

        Optional<User> result = updateUserService.execute(id, inputUser);

        assertThat(result).contains(updatedUser);
        verify(userMapper).toEntity(inputUser);
        verify(userRepository).update(id, inputEntity);
        verify(userMapper).toDomain(updatedEntity);
    }

    @Test
    void executeShouldReturnEmptyWhenUserDoesNotExist() {
        Long id = 404L;
        User inputUser = new User(id, "Unknown", "unknown@example.com");
        UserEntity inputEntity = new UserEntity(id, "Unknown", "unknown@example.com");

        when(userMapper.toEntity(inputUser)).thenReturn(inputEntity);
        when(userRepository.update(id, inputEntity)).thenReturn(Optional.empty());

        Optional<User> result = updateUserService.execute(id, inputUser);

        assertThat(result).isEmpty();
        verify(userMapper).toEntity(inputUser);
        verify(userRepository).update(id, inputEntity);
    }
}
