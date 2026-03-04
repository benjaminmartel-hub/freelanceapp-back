package com.freelanceos.freelanceappback.domain.service.user;

import com.freelanceos.freelanceappback.domain.model.User;
import com.freelanceos.freelanceappback.domain.ports.in.user.GetAllUsersUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllUsersService implements GetAllUsersUseCase {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public GetAllUsersService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<User> execute() {
        return userRepository.findAll().stream()
                .map(userMapper::toDomain)
                .toList();
    }
}
