package com.kihomura.screenvault.security;

import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JWTTokenProvider {

    private final String jwtSecret = generateSecureKey();
    private final long jwtExpirationInMs = 30L * 24 * 60 * 60 * 1000; // 30 days

    private static String generateSecureKey() {
        byte[] keyBytes = new byte[64];
        new SecureRandom().nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
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
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException |
                 ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
        }
        return false;
    }
}
