package com.travel.explorer.server.service;

import com.travel.explorer.server.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final Key signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * สร้าง JWT token
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        long exp = now + jwtProperties.getExpiration();

        return Jwts.builder()
                .setSubject(subject)     // ใช้ email เป็น subject
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(exp))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }


    // ==========================================================
    // 🔥 เพิ่มส่วนนี้ เพื่อให้ TripController ใช้งานได้
    // ==========================================================

    /**
     * ดึง email (subject) ออกจาก token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * ใช้ฟังก์ชันแบบ generic เพื่อดึง claim ใด ๆ
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * คืน claims ทั้งหมดจาก JWT token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}