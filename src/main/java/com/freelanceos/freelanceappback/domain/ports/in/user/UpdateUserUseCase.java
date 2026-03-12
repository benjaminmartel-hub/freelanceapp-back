package com.freelanceos.freelanceappback.domain.ports.in.user;

import com.freelanceos.freelanceappback.domain.model.user.User;

import java.util.Optional;

public interface UpdateUserUseCase {
    Optional<User> execute(Long id, User userToUpdate);
}
