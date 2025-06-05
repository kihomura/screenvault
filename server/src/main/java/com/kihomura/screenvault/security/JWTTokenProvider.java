package com.kihomura.screenvault.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JWTTokenProvider {

    private final SecretKey jwtSecret;
    private final long jwtExpirationInMs;

    // Constructor injection to use configuration values
    public JWTTokenProvider(@Value("${app.jwt.secret}") String jwtSecretString,
                           @Value("${app.jwt.expiration}") long jwtExpiration) {
        // Ensure the secret is at least 512 bits (64 bytes) for HS512
        byte[] keyBytes = jwtSecretString.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 64) {
            // Pad the key to 64 bytes for HS512 compatibility
            byte[] paddedKey = new byte[64];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 64));
            // Fill remaining bytes with a pattern based on the original key
            for (int i = keyBytes.length; i < 64; i++) {
                paddedKey[i] = (byte) (keyBytes[i % keyBytes.length] ^ i);
            }
            keyBytes = paddedKey;
        }
        this.jwtSecret = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpirationInMs = jwtExpiration;
        
        // Log key length for debugging
        System.out.println("JWT Secret key length: " + keyBytes.length + " bytes (" + (keyBytes.length * 8) + " bits)");
    }

    public String generateToken(Authentication authentication) {
        String username;
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
            String registrationId = oauthToken.getAuthorizedClientRegistrationId();

            if ("github".equalsIgnoreCase(registrationId)) {
                username = (String) attributes.get("login");
            } else if ("google".equalsIgnoreCase(registrationId)) {
                username = (String) attributes.get("email");
            } else {
                username = (String) attributes.get("sub");
            }
        } else {
            username = authentication.getName();
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(jwtSecret, Jwts.SIG.HS512)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(jwtSecret)
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Log the exception for debugging
            System.err.println("JWT validation failed: " + ex.getMessage());
        }
        return false;
    }
}
