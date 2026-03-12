package com.freelanceos.freelanceappback.infrastructure.security;

import com.freelanceos.freelanceappback.domain.model.auth.AuthProvider;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.AuthAccountEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final AuthAccountRepository authAccountRepository;

    public DatabaseUserDetailsService(AuthAccountRepository authAccountRepository) {
        this.authAccountRepository = authAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthAccountEntity account = authAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (account.getProvider() != AuthProvider.LOCAL || account.getPasswordHash() == null) {
            throw new UsernameNotFoundException("User has no local credentials");
        }

        return User.withUsername(account.getUsername())
                .password(account.getPasswordHash())
                .roles("USER")
                .build();
    }
}
