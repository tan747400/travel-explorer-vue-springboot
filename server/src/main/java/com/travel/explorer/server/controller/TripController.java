package com.travel.explorer.server.controller;

import com.travel.explorer.server.dto.TripCreateRequest;
import com.travel.explorer.server.dto.TripResponse;
import com.travel.explorer.server.entity.User;
import com.travel.explorer.server.repository.UserRepository;
import com.travel.explorer.server.service.JwtService;
import com.travel.explorer.server.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TripController {

    private final TripService tripService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    // ---------------------------
    // GET /api/trips (Landing + Search)
    // ---------------------------
    @GetMapping
    public Page<TripResponse> getTrips(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size
    ) {
        return tripService.getTrips(keyword, page, size);
    }

    // ---------------------------
    // GET /api/trips/{id} (Detail)
    // ---------------------------
    @GetMapping("/{id}")
    public TripResponse getTrip(@PathVariable Long id) {
        return tripService.getTripById(id);
    }

    // ---------------------------
    // GET /api/trips/mine (Dashboard)
    // ---------------------------
    @GetMapping("/mine")
    public ResponseEntity<List<TripResponse>> getMyTrips(
            @RequestHeader(name = "Authorization", required = false) String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);

        String email;
        try {
            email = jwtService.extractUsername(token);
        } catch (Exception e) {
            // token เสีย / หมดอายุ / verify ไม่ผ่าน
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        List<TripResponse> trips = tripService.getTripsOfUser(user);
        return ResponseEntity.ok(trips);
    }

    // ---------------------------
    // POST /api/trips (Create Trip)
    // ---------------------------
    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @Valid @RequestBody TripCreateRequest req
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);

        String email;
        try {
            email = jwtService.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        TripResponse created = tripService.createTrip(req, user);
        return ResponseEntity.status(201).body(created);
    }
}