package com.freelanceos.freelanceappback.domain.model;

public class AuthAccount {
    private Long id;
    private String username;
    private String passwordHash;
    private AuthProvider provider;
    private String providerUserId;

    public AuthAccount() {
    }

    public AuthAccount(String username, AuthProvider provider) {
        this.username = username;
        this.provider = provider;
    }

    public AuthAccount(Long id, String username, String passwordHash, AuthProvider provider, String providerUserId) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.provider = provider;
        this.providerUserId = providerUserId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }
}
