package com.travel.explorer.server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugEnvController {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @GetMapping("/debug/env")
    public String debug() {
        return "JWT_SECRET = " + jwtSecret;
    }
}