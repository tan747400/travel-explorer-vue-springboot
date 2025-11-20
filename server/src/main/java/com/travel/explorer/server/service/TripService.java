package com.travel.explorer.server.service;

import com.travel.explorer.server.dto.TripCreateRequest;
import com.travel.explorer.server.dto.TripResponse;
import com.travel.explorer.server.entity.Trip;
import com.travel.explorer.server.entity.User;
import com.travel.explorer.server.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;

    /**
     * ดึง trips แบบมี pagination + keyword search
     * ใช้กับหน้า Landing page + Search
     */
    public Page<TripResponse> getTrips(String keyword, int page, int size) {

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

        return tripPage.map(this::toResponse);
    }

    /**
     * ดึง trip เดี่ยวตาม id
     * ใช้กับหน้า Detail (/trips/{id})
     */
    public TripResponse getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        return toResponse(trip);
    }

    /**
     * ดึงรายการทริปของ user คนเดียว (ใช้ใน Dashboard: /api/trips/mine)
     */
    public List<TripResponse> getTripsOfUser(User user) {
        List<Trip> trips = tripRepository.findByAuthorOrderByIdDesc(user);

        return trips.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * สร้างทริปใหม่ผูกกับ owner (ใช้กับ POST /api/trips)
     */
    public TripResponse createTrip(TripCreateRequest req, User owner) {

        Trip trip = Trip.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .province(req.getProvince())
                .tags(req.getTags())               // add
                .latitude(req.getLatitude())       // add
                .longitude(req.getLongitude())     // add
                .author(owner)
                .build();

        Trip saved = tripRepository.save(trip);
        return toResponse(saved);
    }

    // -------------------------
    // helper: map Entity -> DTO
    // -------------------------
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