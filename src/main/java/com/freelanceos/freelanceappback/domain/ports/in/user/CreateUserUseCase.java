package com.freelanceos.freelanceappback.domain.ports.in.user;

import com.freelanceos.freelanceappback.domain.model.user.User;

public interface CreateUserUseCase {
    User execute(User userToCreate);
}
