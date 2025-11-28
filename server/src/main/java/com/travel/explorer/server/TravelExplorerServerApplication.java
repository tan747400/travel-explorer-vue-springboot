package com.travel.explorer.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class TravelExplorerServerApplication {

    // ดึงค่าจาก application-local.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    public static void main(String[] args) {
        SpringApplication.run(TravelExplorerServerApplication.class, args);
    }

    @PostConstruct
    void printJwtSecret() {
        // พิมพ์แค่บางส่วนกัน secret หลุดเต็ม ๆ
        System.out.println(">>> JWT_SECRET loaded = "
                + (jwtSecret != null ? jwtSecret.substring(0, Math.min(10, jwtSecret.length())) + "..." : "null"));
    }
}