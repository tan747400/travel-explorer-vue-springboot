package com.travel.explorer.server.controller;

import com.travel.explorer.server.dto.TripCreateRequest;
import com.travel.explorer.server.dto.TripUpdateRequest;
import com.travel.explorer.server.dto.TripResponse;
import com.travel.explorer.server.dto.TripMetaResponse;
import com.travel.explorer.server.dto.DeletePhotoRequest;
import com.travel.explorer.server.entity.User;
import com.travel.explorer.server.exception.ForbiddenException;
import com.travel.explorer.server.repository.UserRepository;
import com.travel.explorer.server.service.JwtService;
import com.travel.explorer.server.service.TripService;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TripController {

    private final TripService tripService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    // =======================
    //   GET /api/trips/meta
    // =======================
    @GetMapping("/meta")
    public TripMetaResponse getTripsMeta() {
        return tripService.getTripsMeta();
    }

    // =======================
    //   GET /api/trips
    // =======================
    @GetMapping
    public Page<TripResponse> getTrips(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size
    ) {
        return tripService.getTrips(keyword, page, size);
    }

    @GetMapping("/{id}")
    public TripResponse getTrip(@PathVariable Long id) {
        return tripService.getTripById(id);
    }

    // =======================
    //   GET /api/trips/mine
    // =======================
    @GetMapping("/mine")
    public ResponseEntity<List<TripResponse>> getMyTrips(
            @RequestHeader(name = "Authorization", required = false) String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) return ResponseEntity.status(401).build();

        String email;
        try {
            email = jwtService.extractUsername(token);
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        List<TripResponse> trips = tripService.getTripsOfUser(user);
        return ResponseEntity.ok(trips);
    }

    // =======================
    //   POST /api/trips
    // =======================
    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @Valid @RequestBody TripCreateRequest req
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(401).build();

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) return ResponseEntity.status(401).build();

        String email;
        try {
            email = jwtService.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        TripResponse created = tripService.createTrip(req, user);
        return ResponseEntity.status(201).body(created);
    }

    // =======================
    //   PUT /api/trips/{id}
    // =======================
    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(
            @PathVariable Long id,
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @Valid @RequestBody TripUpdateRequest req
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(401).build();

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) return ResponseEntity.status(401).build();

        String email;
        try {
            email = jwtService.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        try {
            TripResponse updated = tripService.updateTrip(id, req, user);
            return ResponseEntity.ok(updated);
        } catch (ForbiddenException e) {
            return ResponseEntity.status(403).build();
        } catch (RuntimeException e) {
            if ("Trip not found".equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            throw e;
        }
    }

    // =======================
    //   DELETE /api/trips/{id}
    // =======================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(
            @PathVariable Long id,
            @RequestHeader(name = "Authorization", required = false) String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(401).build();

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) return ResponseEntity.status(401).build();

        String email;
        try {
            email = jwtService.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        try {
            tripService.deleteTrip(id, user);
            return ResponseEntity.noContent().build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(403).build();
        } catch (RuntimeException e) {
            if ("Trip not found".equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            throw e;
        }
    }

    // =======================================
    //   POST /api/trips/{id}/photos
    //   อัปโหลดรูปเพิ่มในทริป
    // =======================================
    @PostMapping(
            value = "/{id}/photos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<TripResponse> uploadTripPhotos(
            @PathVariable Long id,
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @RequestParam("files") List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty())
            return ResponseEntity.badRequest().build();

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(401).build();

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) return ResponseEntity.status(401).build();

        String email;
        try {
            email = jwtService.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        try {
            TripResponse updated = tripService.uploadTripPhotos(id, files, user);
            return ResponseEntity.ok(updated);
        } catch (ForbiddenException e) {
            return ResponseEntity.status(403).build();
        } catch (RuntimeException e) {
            if ("Trip not found".equals(e.getMessage()))
                return ResponseEntity.notFound().build();
            throw e;
        }
    }

    // =======================================
    //   DELETE /api/trips/{id}/photos
    //   ลบรูปเดิมที่อัปโหลดในทริป (รับ JSON body)
    // =======================================
    @DeleteMapping("/{id}/photos")
    public ResponseEntity<TripResponse> deleteTripPhoto(
            @PathVariable Long id,
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @Valid @RequestBody DeletePhotoRequest request
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

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

        try {
            TripResponse updated = tripService.deleteTripPhoto(
                    id,
                    request.getImageUrl(),
                    user
            );
            return ResponseEntity.ok(updated);
        } catch (ForbiddenException e) {
            return ResponseEntity.status(403).build();
        } catch (RuntimeException e) {
            if ("Trip not found".equals(e.getMessage())) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }
}