package com.freelanceos.freelanceappback.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtTokenService {
    private final SecretKey secretKey;
    private final long expirationSeconds;

    public JwtTokenService(@Value("${app.security.jwt.secret}") String secret,
                           @Value("${app.security.jwt.expiration-seconds:3600}") long expirationSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(String username, String provider) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(expirationSeconds);

        return Jwts.builder()
                .setSubject(username)
                .claim("provider", provider)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
