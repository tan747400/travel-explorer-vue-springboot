package com.travel.explorer.server.service;

import com.travel.explorer.server.dto.TripResponse;
import com.travel.explorer.server.entity.Trip;
import com.travel.explorer.server.repository.TripRepository;  // 👈 ต้องเป็นแบบนี้

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;

    public List<TripResponse> getTrips(String keyword) {
        List<Trip> trips = (keyword == null || keyword.isBlank())
                ? tripRepository.findAll()
                : tripRepository.search(keyword.toLowerCase());

        return trips.stream()
                .map(this::toResponse)
                .toList();
    }

    public TripResponse getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        return toResponse(trip);
    }

    private TripResponse toResponse(Trip t) {
        return TripResponse.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .photos(t.getPhotos())
                .tags(t.getTags())
                .latitude(t.getLatitude())
                .longitude(t.getLongitude())
                .province(t.getProvince())
                .authorName(t.getAuthor() != null ? t.getAuthor().getDisplayName() : "Unknown")
                .build();
    }
}