package com.travel.explorer.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * secret key สำหรับลงนาม JWT (HMAC)
     */
    private String secret;

    /**
     * อายุ access token (ms)
     */
    private long expiration;

    /**
     * อายุ refresh token (ms) – ยังไม่ใช้ตอนนี้ แต่อ่านเก็บไว้
     */
    private long refreshExpiration;

    // ============ getter / setter ============

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    public void setRefreshExpiration(long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }
}