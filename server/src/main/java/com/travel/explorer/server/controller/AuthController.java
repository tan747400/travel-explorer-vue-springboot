package com.travel.explorer.server.controller;

import com.travel.explorer.server.dto.auth.AuthResponse;
import com.travel.explorer.server.dto.auth.ChangePasswordRequest;
import com.travel.explorer.server.dto.auth.LoginRequest;
import com.travel.explorer.server.dto.auth.RegisterRequest;
import com.travel.explorer.server.entity.User;
import com.travel.explorer.server.repository.UserRepository;
import com.travel.explorer.server.service.AuthService;
import com.travel.explorer.server.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthService authService;

    // ถ้ายังไม่มี bean สำหรับ encoder และอยากใช้แบบง่าย ๆ
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // =========================
    //   POST /api/auth/register
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "อีเมลนี้ถูกใช้สมัครแล้ว"));
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName())
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(
                user.getEmail(),
                Map.of(
                        "userId", user.getId(),
                        "displayName", user.getDisplayName()
                )
        );

        AuthResponse res = AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // =========================
    //   POST /api/auth/login
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "อีเมลหรือรหัสผ่านไม่ถูกต้อง"));
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                Map.of(
                        "userId", user.getId(),
                        "displayName", user.getDisplayName()
                )
        );

        AuthResponse res = AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .build();

        return ResponseEntity.ok(res);
    }

    // ==================================
    //   POST /api/auth/change-password
    // ==================================
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "ไม่พบโทเคนหรือโทเคนไม่ถูกต้อง"));
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "โทเคนไม่ถูกต้อง"));
        }

        String email;
        try {
            email = jwtService.extractUsername(token);
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "โทเคนไม่ถูกต้องหรือหมดอายุแล้ว"));
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "ไม่พบผู้ใช้งาน"));
        }

        try {
            authService.changePassword(user, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "เปลี่ยนรหัสผ่านสำเร็จ"));
        } catch (IllegalArgumentException ex) {
            // ใช้ message ตรง ๆ เป็น feedback ให้ user
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", ex.getMessage()));
        }
    }
}