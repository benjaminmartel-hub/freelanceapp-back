package com.freelanceos.freelanceappback.application.rest;

import com.freelanceos.freelanceappback.application.rest.dto.AuthResponse;
import com.freelanceos.freelanceappback.application.rest.dto.AuthMeResponse;
import com.freelanceos.freelanceappback.application.rest.dto.LoginRequest;
import com.freelanceos.freelanceappback.application.rest.dto.RegisterRequest;
import com.freelanceos.freelanceappback.application.rest.mapper.AuthMapperRest;
import com.freelanceos.freelanceappback.domain.model.AuthAccount;
import com.freelanceos.freelanceappback.domain.ports.in.auth.GetCurrentAuthenticatedUserUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.auth.LoginWithPasswordUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.auth.RegisterWithPasswordUseCase;
import com.freelanceos.freelanceappback.infrastructure.security.JwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final RegisterWithPasswordUseCase registerWithPasswordUseCase;
    private final LoginWithPasswordUseCase loginWithPasswordUseCase;
    private final GetCurrentAuthenticatedUserUseCase getCurrentAuthenticatedUserUseCase;
    private final JwtTokenService jwtTokenService;
    private final AuthMapperRest authMapperRest;

    public AuthController(RegisterWithPasswordUseCase registerWithPasswordUseCase,
                          LoginWithPasswordUseCase loginWithPasswordUseCase,
                          GetCurrentAuthenticatedUserUseCase getCurrentAuthenticatedUserUseCase,
                          JwtTokenService jwtTokenService,
                          AuthMapperRest authMapperRest) {
        this.registerWithPasswordUseCase = registerWithPasswordUseCase;
        this.loginWithPasswordUseCase = loginWithPasswordUseCase;
        this.getCurrentAuthenticatedUserUseCase = getCurrentAuthenticatedUserUseCase;
        this.jwtTokenService = jwtTokenService;
        this.authMapperRest = authMapperRest;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest registerRequest) {
        try {
            AuthAccount result = registerWithPasswordUseCase.execute(
                    registerRequest.getUsername(),
                    registerRequest.getPassword()
            );
            LOGGER.info("User registered with username={}", result.getUsername());
            String token = jwtTokenService.generateToken(result.getUsername(), result.getProvider().name());
            return authMapperRest.toResponse(token);
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        try {
            AuthAccount result = loginWithPasswordUseCase.execute(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );
            LOGGER.info("User logged in with username={}", result.getUsername());
            String token = jwtTokenService.generateToken(result.getUsername(), result.getProvider().name());
            return authMapperRest.toResponse(token);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }

    @GetMapping("/me")
    public AuthMeResponse me(Principal principal) {
        String username = principal != null ? principal.getName() : null;
        if (username == null || username.isBlank()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                username = authentication.getName();
            }
        }

        if (username == null || username.isBlank() || "anonymousUser".equals(username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        try {
            AuthAccount result = getCurrentAuthenticatedUserUseCase.execute(username);
            return new AuthMeResponse(result.getUsername(), result.getProvider().name());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }
}
