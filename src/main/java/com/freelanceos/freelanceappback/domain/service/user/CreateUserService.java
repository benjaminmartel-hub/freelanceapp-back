package com.freelanceos.freelanceappback.domain.service.user;

import com.freelanceos.freelanceappback.domain.model.User;
import com.freelanceos.freelanceappback.domain.ports.in.user.CreateUserUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class CreateUserService implements CreateUserUseCase {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public CreateUserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User execute(User userToCreate) {
        UserEntity savedUser = userRepository.save(userMapper.toEntity(userToCreate));
        return userMapper.toDomain(savedUser);
    }
}
