package com.travel.explorer.server.controller;

import com.travel.explorer.server.dto.TripCreateRequest;
import com.travel.explorer.server.dto.TripUpdateRequest;
import com.travel.explorer.server.dto.TripResponse;
import com.travel.explorer.server.entity.User;
import com.travel.explorer.server.exception.ForbiddenException;
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

    // GET /api/trips?keyword=&page=0&size=6
    @GetMapping
    public Page<TripResponse> getTrips(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size
    ) {
        return tripService.getTrips(keyword, page, size);
    }

    // GET /api/trips/{id}
    @GetMapping("/{id}")
    public TripResponse getTrip(@PathVariable Long id) {
        return tripService.getTripById(id);
    }

    // GET /api/trips/mine
    @GetMapping("/mine")
    public ResponseEntity<List<TripResponse>> getMyTrips(
            @RequestHeader(name = "Authorization", required = false) String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        List<TripResponse> trips = tripService.getTripsOfUser(user);
        return ResponseEntity.ok(trips);
    }

    // POST /api/trips  (สร้างทริปใหม่)
    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @Valid @RequestBody TripCreateRequest req
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        TripResponse created = tripService.createTrip(req, user);
        return ResponseEntity.status(201).body(created);
    }

    // PUT /api/trips/{id}  (แก้ไขทริป)
    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(
            @PathVariable Long id,
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @Valid @RequestBody TripUpdateRequest req
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            TripResponse updated = tripService.updateTrip(id, req, user);
            return ResponseEntity.ok(updated);
        } catch (ForbiddenException e) {               // จับ Forbidden ก่อน
            return ResponseEntity.status(403).build();
        } catch (RuntimeException e) {                  // แล้วค่อย RuntimeException
            if ("Trip not found".equals(e.getMessage())) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    // DELETE /api/trips/{id}  (ลบทริป)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(
            @PathVariable Long id,
            @RequestHeader(name = "Authorization", required = false) String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            tripService.deleteTrip(id, user);
            return ResponseEntity.noContent().build();
        } catch (ForbiddenException e) {                // จับ Forbidden ก่อน
            return ResponseEntity.status(403).build();
        } catch (RuntimeException e) {                  // แล้วค่อย RuntimeException
            if ("Trip not found".equals(e.getMessage())) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }
}