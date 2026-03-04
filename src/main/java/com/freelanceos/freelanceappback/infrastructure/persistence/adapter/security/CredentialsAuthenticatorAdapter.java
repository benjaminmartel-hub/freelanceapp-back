package com.freelanceos.freelanceappback.infrastructure.persistence.adapter.security;

import com.freelanceos.freelanceappback.domain.ports.out.security.CredentialsAuthenticator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CredentialsAuthenticatorAdapter implements CredentialsAuthenticator {
    private final AuthenticationManager authenticationManager;

    public CredentialsAuthenticatorAdapter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public boolean authenticate(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (AuthenticationException ex) {
            return false;
        }
    }
}
