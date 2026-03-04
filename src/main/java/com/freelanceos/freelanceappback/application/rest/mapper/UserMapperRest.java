package com.freelanceos.freelanceappback.application.rest.mapper;

import com.freelanceos.freelanceappback.application.rest.dto.UserRequest;
import com.freelanceos.freelanceappback.application.rest.dto.UserResponse;
import com.freelanceos.freelanceappback.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperRest {
    public User toDomain(UserRequest userRequest) {
        return new User(null, userRequest.getName(), userRequest.getEmail());
    }

    public User toDomain(Long id, UserRequest userRequest) {
        return new User(id, userRequest.getName(), userRequest.getEmail());
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
