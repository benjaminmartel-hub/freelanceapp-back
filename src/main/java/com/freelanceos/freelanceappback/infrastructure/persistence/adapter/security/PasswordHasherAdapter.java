package com.freelanceos.freelanceappback.infrastructure.persistence.adapter.security;

import com.freelanceos.freelanceappback.domain.ports.out.security.PasswordHasher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasherAdapter implements PasswordHasher {
    private final PasswordEncoder passwordEncoder;

    public PasswordHasherAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
