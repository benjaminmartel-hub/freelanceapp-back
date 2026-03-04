package com.freelanceos.freelanceappback.domain.ports.in.user;

import com.freelanceos.freelanceappback.domain.model.User;

import java.util.Optional;

public interface GetUserByIdUseCase {
    Optional<User> execute(Long id);
}
