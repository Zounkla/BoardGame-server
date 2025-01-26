package com.boardgame.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateJwtToken(Authentication authentication) {
        String username = authentication.getName();
        long jwtExpirationMs = 86400000;
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        Claims claims = parseJwtToken(token);
        return claims.getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Claims claims = parseJwtToken(token);
            Date expirationDate = claims.getExpiration();
            return !expirationDate.before(new Date());
        } catch (Exception e) {
            System.err.println("Erreur lors de la validation du token JWT : " + e.getMessage());
        }
        return false;
    }

    private Claims parseJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
