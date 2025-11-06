package com.example.user_service.Security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
// Importe esta classe (a mesma do denuncia-service)
import jakarta.annotation.PostConstruct;

@Component 
public class JwtUtil {

    @Value("${jwt.secret}") // 2. Lê a chave do application.properties
    private String secret;

    private Key key;
    private static final long EXPIRATION_TIME = 86400000; // 1 dia

    @PostConstruct 
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 4. O método agora NÃO é mais 'static'
    public String generateToken(int userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key) 
                .compact();
    }


    public int getUserIdFromToken(String token) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return Integer.parseInt(subject);
    }
}