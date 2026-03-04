package com.freelanceos.freelanceappback.domain.ports.out.security;

public interface CredentialsAuthenticator {
    boolean authenticate(String username, String password);
}
