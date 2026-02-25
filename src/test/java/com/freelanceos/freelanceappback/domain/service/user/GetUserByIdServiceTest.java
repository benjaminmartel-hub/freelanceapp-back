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
class GetUserByIdServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private GetUserByIdService getUserByIdService;

    @Test
    void executeShouldReturnMappedUserWhenFound() {
        Long id = 1L;
        UserEntity entity = new UserEntity(id, "Alice", "alice@example.com");
        User user = new User(id, "Alice", "alice@example.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(entity));
        when(userMapper.toDomain(entity)).thenReturn(user);

        Optional<User> result = getUserByIdService.execute(id);

        assertThat(result).contains(user);
        verify(userRepository).findById(id);
        verify(userMapper).toDomain(entity);
    }

    @Test
    void executeShouldReturnEmptyWhenMissing() {
        Long id = 404L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Optional<User> result = getUserByIdService.execute(id);

        assertThat(result).isEmpty();
        verify(userRepository).findById(id);
    }
}
