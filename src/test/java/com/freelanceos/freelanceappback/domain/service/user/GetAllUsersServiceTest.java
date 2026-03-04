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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllUsersServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private GetAllUsersService getAllUsersService;

    @Test
    void executeShouldReturnMappedUsers() {
        UserEntity entity1 = new UserEntity(1L, "Alice", "alice@example.com");
        UserEntity entity2 = new UserEntity(2L, "Bob", "bob@example.com");
        User user1 = new User(1L, "Alice", "alice@example.com");
        User user2 = new User(2L, "Bob", "bob@example.com");

        when(userRepository.findAll()).thenReturn(List.of(entity1, entity2));
        when(userMapper.toDomain(entity1)).thenReturn(user1);
        when(userMapper.toDomain(entity2)).thenReturn(user2);

        List<User> result = getAllUsersService.execute();

        assertThat(result).containsExactly(user1, user2);
        verify(userRepository).findAll();
        verify(userMapper).toDomain(entity1);
        verify(userMapper).toDomain(entity2);
    }
}
