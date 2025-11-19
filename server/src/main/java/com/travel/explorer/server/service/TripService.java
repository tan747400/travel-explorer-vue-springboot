package com.travel.explorer.server.service;

import com.travel.explorer.server.dto.TripResponse;
import com.travel.explorer.server.entity.Trip;
import com.travel.explorer.server.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;

    /**
     * ดึง trips แบบมี pagination + keyword search
     */
    public Page<TripResponse> getTrips(String keyword, int page, int size) {

        // ให้ sort id จากมากไปน้อย (ทริปล่าสุดอยู่บน)
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<Trip> tripPage;

        if (keyword == null || keyword.isBlank()) {
            tripPage = tripRepository.findAll(pageable);
        } else {
            tripPage = tripRepository.search(keyword.toLowerCase(), pageable);
        }

        // map Trip -> TripResponse แล้วคง structure Page เดิมไว้
        return tripPage.map(this::toResponse);
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