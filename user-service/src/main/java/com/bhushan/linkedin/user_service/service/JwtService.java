package com.bhushan.linkedin.user_service.service;


import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

import com.bhushan.linkedin.user_service.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {


    @Value("${jwt.secretKey}")	///this will come from application.propeties file
    private String jwtSecretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())	// to identify the user
                .claim("email", user.getEmail())	// claims are key value pairs
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*100))
                .signWith(getSecretKey())	// this will basically pass the key to an algorithm
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey()) // it will verify the
                .build()
                .parseSignedClaims(token) // it will validate the token
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }

}
