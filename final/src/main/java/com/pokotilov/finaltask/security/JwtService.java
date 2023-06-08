package com.pokotilov.finaltask.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    boolean isTokenValid(String token, UserDetails user);

    String generateToken(UserDetails user);

    String generateToken(Map<String, Object> extraClaims, UserDetails user);

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}
