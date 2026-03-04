package com.freelanceos.freelanceappback.application.rest.mapper;

import com.freelanceos.freelanceappback.application.rest.dto.AuthResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthMapperRest {
    public AuthResponse toResponse(String token) {
        return new AuthResponse(token);
    }
}
