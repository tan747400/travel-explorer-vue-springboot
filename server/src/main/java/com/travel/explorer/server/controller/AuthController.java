package com.travel.explorer.server.controller;

import com.travel.explorer.server.dto.auth.AuthResponse;
import com.travel.explorer.server.dto.auth.LoginRequest;
import com.travel.explorer.server.dto.auth.RegisterRequest;
import com.travel.explorer.server.entity.User;
import com.travel.explorer.server.repository.UserRepository;
import com.travel.explorer.server.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")   // ✅ เพิ่มบรรทัดนี้
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        // เช็คซ้ำ email
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setDisplayName(req.getDisplayName());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        user = userRepository.save(user);

        String token = jwtService.generateToken(
                user.getId().toString(),
                Map.of(
                        "email", user.getEmail(),
                        "displayName", user.getDisplayName()
                )
        );

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getDisplayName()
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getId().toString(),
                Map.of(
                        "email", user.getEmail(),
                        "displayName", user.getDisplayName()
                )
        );

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getDisplayName()
        );
    }
}