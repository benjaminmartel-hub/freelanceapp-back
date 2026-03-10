package com.freelanceos.freelanceappback.infrastructure.security;

import com.freelanceos.freelanceappback.domain.model.auth.AuthProvider;
import com.freelanceos.freelanceappback.domain.model.auth.AuthAccount;
import com.freelanceos.freelanceappback.domain.ports.in.auth.LoginWithOAuthUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OAuth2AuthenticationSuccessHandlerTest {

    @Test
    void onAuthenticationSuccessShouldRedirectWithApplicationJwtToken() throws Exception {
        LoginWithOAuthUseCase loginWithOAuthUseCase = mock(LoginWithOAuthUseCase.class);
        JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        OAuth2AuthenticationSuccessHandler handler = new OAuth2AuthenticationSuccessHandler(
                loginWithOAuthUseCase,
                jwtTokenService,
                "http://localhost:4200/auth/oauth2/callback"
        );

        OAuth2User principal = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("sub", "oauth-sub-123", "email", "alice@example.com"),
                "sub"
        );
        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                principal,
                principal.getAuthorities(),
                "google"
        );

        when(loginWithOAuthUseCase.execute(AuthProvider.GOOGLE, "oauth-sub-123", "alice@example.com"))
                .thenReturn(new AuthAccount("alice@example.com", AuthProvider.GOOGLE));
        when(jwtTokenService.generateToken("alice@example.com", "GOOGLE")).thenReturn("app-jwt-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        handler.onAuthenticationSuccess(new MockHttpServletRequest(), response, authentication);

        verify(loginWithOAuthUseCase).execute(eq(AuthProvider.GOOGLE), eq("oauth-sub-123"), eq("alice@example.com"));
        verify(jwtTokenService).generateToken("alice@example.com", "GOOGLE");
        org.junit.jupiter.api.Assertions.assertEquals(
                "http://localhost:4200/auth/oauth2/callback?token=app-jwt-token",
                response.getRedirectedUrl()
        );
    }
}
