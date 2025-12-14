package com.viddefe.viddefe_api.config.Components;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET;
    private final long EXPIRATION = 86400000;

    private Key getKey(){
        return  Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String email,String role, String firstName, String lastName, UUID userId) {
        Map<String, String > claims = Map.of(
                "role", role,
                "first_name", firstName,
                "last_name", lastName,
                "userId", userId.toString()
        );

        return Jwts.builder()
                .header()
                .keyId(UUID.randomUUID().toString())
                .and()
                .subject(email)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}