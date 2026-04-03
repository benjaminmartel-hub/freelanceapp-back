package com.freelanceos.freelanceappback.application.rest;

import com.freelanceos.freelanceappback.application.rest.dto.auth.AuthResponse;
import com.freelanceos.freelanceappback.application.rest.dto.auth.AuthMeResponse;
import com.freelanceos.freelanceappback.application.rest.dto.auth.LoginRequest;
import com.freelanceos.freelanceappback.application.rest.dto.auth.RegisterRequest;
import com.freelanceos.freelanceappback.application.rest.mapper.AuthMapperRest;
import com.freelanceos.freelanceappback.domain.exception.ConflictException;
import com.freelanceos.freelanceappback.domain.exception.UnauthorizedException;
import com.freelanceos.freelanceappback.domain.model.auth.AuthAccount;
import com.freelanceos.freelanceappback.domain.ports.in.auth.GetCurrentAuthenticatedUserUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.auth.LoginWithPasswordUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.auth.RegisterWithPasswordUseCase;
import com.freelanceos.freelanceappback.infrastructure.security.JwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    private final AuthenticatedUserResolver authenticatedUserResolver;

    public AuthController(RegisterWithPasswordUseCase registerWithPasswordUseCase,
                          LoginWithPasswordUseCase loginWithPasswordUseCase,
                          GetCurrentAuthenticatedUserUseCase getCurrentAuthenticatedUserUseCase,
                          JwtTokenService jwtTokenService,
                          AuthMapperRest authMapperRest,
                          AuthenticatedUserResolver authenticatedUserResolver) {
        this.registerWithPasswordUseCase = registerWithPasswordUseCase;
        this.loginWithPasswordUseCase = loginWithPasswordUseCase;
        this.getCurrentAuthenticatedUserUseCase = getCurrentAuthenticatedUserUseCase;
        this.jwtTokenService = jwtTokenService;
        this.authMapperRest = authMapperRest;
        this.authenticatedUserResolver = authenticatedUserResolver;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest registerRequest) {
        try {
            AuthAccount result = registerWithPasswordUseCase.execute(
                    registerRequest.username(),
                    registerRequest.password()
            );
            LOGGER.info("User registered with username={}", result.username());
            String token = jwtTokenService.generateToken(result.username(), result.provider().name());
            return authMapperRest.toResponse(token);
        } catch (ConflictException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        try {
            AuthAccount result = loginWithPasswordUseCase.execute(
                    loginRequest.username(),
                    loginRequest.password()
            );
            LOGGER.info("User logged in with username={}", result.username());
            String token = jwtTokenService.generateToken(result.username(), result.provider().name());
            return authMapperRest.toResponse(token);
        } catch (UnauthorizedException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }

    @GetMapping("/me")
    public AuthMeResponse me(Principal principal) {
        String username = authenticatedUserResolver.resolve(principal);
        try {
            AuthAccount result = getCurrentAuthenticatedUserUseCase.execute(username);
            return new AuthMeResponse(result.username(), result.provider().name());
        } catch (UnauthorizedException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }
}
