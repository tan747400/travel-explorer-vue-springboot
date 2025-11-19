package com.travel.explorer.server.controller;

import com.travel.explorer.server.dto.TripResponse;
import com.travel.explorer.server.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TripController {

    private final TripService tripService;

    /**
     * GET /api/trips?keyword=...&page=0&size=6
     * คืน Page<TripResponse> → frontend ใช้ field content, totalPages, last, number, size
     */
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
}