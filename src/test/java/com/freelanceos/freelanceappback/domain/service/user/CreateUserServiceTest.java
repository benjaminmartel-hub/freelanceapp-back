package com.freelanceos.freelanceappback.domain.service.user;

import com.freelanceos.freelanceappback.domain.model.user.User;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CreateUserService createUserService;

    @Test
    void executeShouldMapAndPersistUser() {
        User inputUser = new User(null, "Alice", "alice@example.com");
        UserEntity mappedEntity = new UserEntity(null, "Alice", "alice@example.com");
        UserEntity savedEntity = new UserEntity(1L, "Alice", "alice@example.com");
        User mappedResult = new User(1L, "Alice", "alice@example.com");

        when(userMapper.toEntity(inputUser)).thenReturn(mappedEntity);
        when(userRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(userMapper.toDomain(savedEntity)).thenReturn(mappedResult);

        User result = createUserService.execute(inputUser);

        assertThat(result).isEqualTo(mappedResult);
        verify(userMapper).toEntity(inputUser);
        verify(userRepository).save(mappedEntity);
        verify(userMapper).toDomain(savedEntity);
    }
}
