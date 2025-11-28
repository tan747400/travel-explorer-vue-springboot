package com.travel.explorer.server.service;

import com.travel.explorer.server.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private Key signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * เตรียม signing key เมื่อ Spring Boot start
     */
    @PostConstruct
    public void init() {
        String secret = jwtProperties.getSecret();
        System.out.println(">>> JWT_SECRET loaded = " + secret);

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        // HMAC-SHA256 ต้องใช้ความยาว >= 32 bytes
        if (keyBytes.length < 32) {
            keyBytes = Arrays.copyOf(keyBytes, 32);
            System.out.println(">>> JWT_SECRET too short, padded to 32 bytes");
        }

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * สร้าง JWT พร้อม claims เพิ่มเติม
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        long exp = now + jwtProperties.getExpiration();

        Map<String, Object> safeClaims = (claims != null) ? claims : Map.of();

        return Jwts.builder()
                .setClaims(safeClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(exp))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String subject) {
        return generateToken(subject, Map.of());
    }

    // =======================
    // Extract claims
    // =======================

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // =======================
    // Validate token
    // =======================

    public boolean isTokenValid(String token, String email) {
        try {
            String username = extractUsername(token);
            return username.equals(email) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}