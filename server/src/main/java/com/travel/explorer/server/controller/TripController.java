package com.travel.explorer.server.controller;

import com.travel.explorer.server.dto.TripResponse;
import com.travel.explorer.server.service.TripService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "http://localhost:5174")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public List<TripResponse> getTrips(
            @RequestParam(name = "keywords", required = false) String keywords
    ) {
        return tripService.getTrips(keywords);
    }

    @GetMapping("/{id}")
    public TripResponse getTrip(@PathVariable Long id) {
        return tripService.getTripById(id);
    }
}