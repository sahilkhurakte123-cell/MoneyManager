package com.example.MoneyManager.utilities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    @PostConstruct
    private void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }


    // Token validity (e.g. 1 day)
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    public String generateToken(String email) {

        return Jwts.builder()
                .setSubject(email)                // who the token belongs to
                .setIssuedAt(new Date())         // token creation time
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)                   // sign token
                .compact();                      // build JWT string
    }

    public String extractUsername(String jwtToken) {
        return extractAllClaims(jwtToken).getSubject();
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    private boolean isTokenExpired(String jwtToken) {
        Date expiration = extractAllClaims(jwtToken).getExpiration();
        return expiration.before(new Date());
    }

    public boolean validateToken(String jwtToken, UserDetails userDetails) {

        final String username = extractUsername(jwtToken);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(jwtToken);
    }

}
