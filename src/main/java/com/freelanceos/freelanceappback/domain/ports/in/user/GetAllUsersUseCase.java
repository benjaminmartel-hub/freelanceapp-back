package com.freelanceos.freelanceappback.domain.ports.in.user;

import com.freelanceos.freelanceappback.domain.model.user.User;

import java.util.List;

public interface GetAllUsersUseCase {
    List<User> execute();
}
