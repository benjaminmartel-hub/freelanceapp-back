package com.freelanceos.freelanceappback.infrastructure.security;

import com.freelanceos.freelanceappback.domain.model.AuthProvider;
import com.freelanceos.freelanceappback.domain.model.AuthAccount;
import com.freelanceos.freelanceappback.domain.ports.in.auth.LoginWithOAuthUseCase;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private final LoginWithOAuthUseCase loginWithOAuthUseCase;
    private final JwtTokenService jwtTokenService;
    private final String oauth2SuccessRedirectUri;

    public OAuth2AuthenticationSuccessHandler(LoginWithOAuthUseCase loginWithOAuthUseCase,
                                              JwtTokenService jwtTokenService,
                                              @Value("${app.security.oauth2.success-redirect-uri:http://localhost:4200/auth/login}") String oauth2SuccessRedirectUri) {
        this.loginWithOAuthUseCase = loginWithOAuthUseCase;
        this.jwtTokenService = jwtTokenService;
        this.oauth2SuccessRedirectUri = oauth2SuccessRedirectUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unsupported authentication type");
            return;
        }

        OAuth2User principal = oauthToken.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();

        String registrationId = oauthToken.getAuthorizedClientRegistrationId().toUpperCase();
        AuthProvider provider = AuthProvider.valueOf(registrationId);
        String providerUserId = resolveProviderUserId(principal);
        String preferredUsername = resolvePreferredUsername(attributes, principal.getName());

        AuthAccount result = loginWithOAuthUseCase.execute(provider, providerUserId, preferredUsername);
        LOGGER.info("OAuth login succeeded for provider={} username={}", provider, result.getUsername());
        String token = jwtTokenService.generateToken(result.getUsername(), result.getProvider().name());
        String redirectUrl = UriComponentsBuilder.fromUriString(oauth2SuccessRedirectUri)
                .queryParam("token", token)
                .build()
                .encode()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    private String resolveProviderUserId(OAuth2User principal) {
        return principal.getName();
    }

    private String resolvePreferredUsername(Map<String, Object> attributes, String fallback) {
        Object email = attributes.get("email");
        if (email instanceof String emailValue && !emailValue.isBlank()) {
            return emailValue;
        }

        Object login = attributes.get("login");
        if (login instanceof String loginValue && !loginValue.isBlank()) {
            return loginValue;
        }

        return fallback;
    }
}
