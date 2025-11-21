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

    @PostConstruct
    public void init() {
        String secret = jwtProperties.getSecret();
        System.out.println(">>> JWT_SECRET loaded = " + secret);

        // ใช้ secret แบบ plain text ไม่ต้อง Base64
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        // ให้ยาวพอสำหรับ HMAC-SHA (อย่างน้อย 32 bytes)
        if (keyBytes.length < 32) {
            keyBytes = Arrays.copyOf(keyBytes, 32);
        }

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * สร้าง JWT token พร้อม claims เพิ่มเติม
     * - subject: ใช้เก็บ email (หรือ user identifier)
     * - claims: ใช้เก็บข้อมูลเพิ่ม เช่น role, displayName ฯลฯ
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

    /** helper แบบไม่ส่ง claims เพิ่ม */
    public String generateToken(String subject) {
        return generateToken(subject, Map.of());
    }

    // =======================
    // ฝั่งอ่านข้อมูลจาก token
    // =======================

    /** ดึง email (subject) ออกจาก token */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}