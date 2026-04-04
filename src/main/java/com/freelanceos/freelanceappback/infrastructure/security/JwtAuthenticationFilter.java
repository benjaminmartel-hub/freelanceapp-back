package com.freelanceos.freelanceappback.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import org.springframework.beans.factory.ObjectProvider;

import java.io.IOException;
import java.util.Set;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenService jwtTokenService;
    private final ObjectProvider<UserRepository> userRepositoryProvider;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService,
                                   ObjectProvider<UserRepository> userRepositoryProvider) {
        this.jwtTokenService = jwtTokenService;
        this.userRepositoryProvider = userRepositoryProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(AUTHORIZATION);
        if (header == null || !header.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        if (token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean tokenValid = jwtTokenService.isTokenValid(token);
        if (tokenValid && SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtTokenService.extractUsername(token);
            UserRepository userRepository = userRepositoryProvider.getIfAvailable();
            Optional<UserEntity> userEntity = Optional.empty();
            if (userRepository != null) {
                userEntity = userRepository.findByNameIgnoreCase(username)
                        .or(() -> userRepository.findByEmailIgnoreCase(username));
            }
            var authorities = userEntity.map(UserEntity::getAuthorities).orElseGet(Set::of);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
