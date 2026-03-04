package com.freelanceos.freelanceappback.domain.service.user;

import com.freelanceos.freelanceappback.domain.model.User;
import com.freelanceos.freelanceappback.domain.ports.in.user.UpdateUserUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateUserService implements UpdateUserUseCase {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UpdateUserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> execute(Long id, User userToUpdate) {
        return userRepository.update(id, userMapper.toEntity(userToUpdate))
                .map(userMapper::toDomain);
    }
}
