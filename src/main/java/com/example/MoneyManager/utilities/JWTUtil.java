package com.example.MoneyManager.utilities;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtil {

    // 🔐 Secret key (move to application.properties later)
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

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
}
