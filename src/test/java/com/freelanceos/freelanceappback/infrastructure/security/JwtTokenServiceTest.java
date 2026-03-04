package com.freelanceos.freelanceappback.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenServiceTest {

    @Test
    void generateTokenShouldSetExpAsEpochSecondsWithConfiguredLifetime() throws Exception {
        long expirationSeconds = 3600;
        String secret = "test-secret-key-for-jwt-signing-32-bytes-min";
        JwtTokenService jwtTokenService = new JwtTokenService(secret, expirationSeconds);

        Instant beforeGeneration = Instant.now();
        String token = jwtTokenService.generateToken("alice", "GOOGLE");
        Instant afterGeneration = Instant.now();

        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        long iat = claims.getIssuedAt().toInstant().getEpochSecond();
        long exp = claims.getExpiration().toInstant().getEpochSecond();
        long actualLifetime = exp - iat;
        assertEquals(expirationSeconds, actualLifetime);
        assertTrue(iat >= beforeGeneration.getEpochSecond() && iat <= afterGeneration.getEpochSecond());

        String payloadJson = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
        Map<String, Object> payload = new ObjectMapper().readValue(payloadJson, Map.class);
        assertTrue(payload.get("exp") instanceof Number);
        assertTrue(payload.get("iat") instanceof Number);
    }
}
