package com.freelanceos.freelanceappback.domain.service.user;

import com.freelanceos.freelanceappback.domain.ports.in.user.DeleteUserUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserService implements DeleteUserUseCase {
    private final UserRepository userRepository;

    public DeleteUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean execute(Long id) {
        return userRepository.deleteById(id);
    }
}
