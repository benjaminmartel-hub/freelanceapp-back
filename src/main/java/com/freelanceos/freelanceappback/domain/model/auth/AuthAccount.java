package com.freelanceos.freelanceappback.domain.model.auth;

public record AuthAccount(Long id, String username, String passwordHash, AuthProvider provider, String providerUserId) {

    public AuthAccount(String username, AuthProvider provider) {
        this(null, username, null, provider, null);
    }
}
