package com.travel.explorer.server.controller;

import com.travel.explorer.server.dto.auth.AuthResponse;
import com.travel.explorer.server.dto.auth.ChangePasswordRequest;
import com.travel.explorer.server.dto.auth.LoginRequest;
import com.travel.explorer.server.dto.auth.RegisterRequest;
import com.travel.explorer.server.dto.auth.UpdateProfileRequest;
import com.travel.explorer.server.entity.User;
import com.travel.explorer.server.repository.UserRepository;
import com.travel.explorer.server.service.AuthService;
import com.travel.explorer.server.service.ImageUploadService;
import com.travel.explorer.server.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthService authService;
    private final ImageUploadService imageUploadService;

    // ใช้จาก SecurityConfig
    private final BCryptPasswordEncoder passwordEncoder;

    // ======================================================
    // REGISTER
    // ======================================================
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
                .profileImageUrl(user.getProfileImageUrl()) // ยัง null
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // ======================================================
    // LOGIN
    // ======================================================
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
                .profileImageUrl(user.getProfileImageUrl())
                .build();

        return ResponseEntity.ok(res);
    }

    // ======================================================
    // CHANGE PASSWORD
    // ======================================================
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        User user = validateTokenAndGetUser(authHeader);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "โทเคนไม่ถูกต้องหรือหมดอายุ"));
        }

        try {
            authService.changePassword(user, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "เปลี่ยนรหัสผ่านสำเร็จ"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    // ======================================================
    // UPDATE PROFILE (update displayName)
    // ======================================================
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        User user = validateTokenAndGetUser(authHeader);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "โทเคนไม่ถูกต้องหรือหมดอายุ"));

        user.setDisplayName(request.getDisplayName());
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
                .profileImageUrl(user.getProfileImageUrl())
                .build();

        return ResponseEntity.ok(res);
    }

    // ======================================================
    // UPLOAD PROFILE PICTURE
    // ======================================================
    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @RequestParam("file") MultipartFile file
    ) {
        User user = validateTokenAndGetUser(authHeader);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "โทเคนไม่ถูกต้องหรือหมดอายุ"));

        try {
            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isBlank()) {
                imageUploadService.deleteImage(user.getProfileImageUrl());
            }

            String folder = "travel-explorer/profile/" + user.getId();
            String url = imageUploadService.uploadImageToFolder(file, folder);

            user.setProfileImageUrl(url);
            userRepository.save(user);

            AuthResponse res = AuthResponse.builder()
                    .token(authHeader.substring(7))
                    .userId(user.getId())
                    .email(user.getEmail())
                    .displayName(user.getDisplayName())
                    .profileImageUrl(url)
                    .build();

            return ResponseEntity.ok(res);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "อัปโหลดรูปโปรไฟล์ไม่สำเร็จ"));
        }
    }

    // ======================================================
    // DELETE PROFILE PICTURE
    // ======================================================
    @DeleteMapping("/profile-picture")
    public ResponseEntity<?> deleteProfilePicture(
            @RequestHeader(name = "Authorization", required = false) String authHeader
    ) {
        User user = validateTokenAndGetUser(authHeader);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "โทเคนไม่ถูกต้องหรือหมดอายุ"));

        try {
            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isBlank()) {
                imageUploadService.deleteImage(user.getProfileImageUrl());
            }

            user.setProfileImageUrl(null);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "ลบรูปโปรไฟล์เรียบร้อย"));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "ลบรูปโปรไฟล์ไม่สำเร็จ"));
        }
    }

    // ======================================================
    // HELPER: validate bearer token
    // ======================================================
    private User validateTokenAndGetUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            return null;
        }

        try {
            String email = jwtService.extractUsername(token);
            return userRepository.findByEmail(email).orElse(null);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}