package com.freelanceos.freelanceappback.domain.ports.out.security;

public interface PasswordHasher {
    String hash(String rawPassword);
}
