package com.travel.explorer.server.service;

import com.travel.explorer.server.dto.TripCreateRequest;
import com.travel.explorer.server.dto.TripUpdateRequest;
import com.travel.explorer.server.dto.TripResponse;
import com.travel.explorer.server.entity.Trip;
import com.travel.explorer.server.entity.User;
import com.travel.explorer.server.exception.ForbiddenException;
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
                .tags(req.getTags())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .author(owner)
                .build();

        Trip saved = tripRepository.save(trip);
        return toResponse(saved);
    }

    /**
     * แก้ไขทริป (PUT /api/trips/{id})
     * ให้แก้ไขได้เฉพาะเจ้าของทริปเท่านั้น
     */
    public TripResponse updateTrip(Long id, TripUpdateRequest req, User currentUser) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // เช็คสิทธิ์: ต้องเป็นเจ้าของทริป
        if (trip.getAuthor() == null ||
                !trip.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not allowed to edit this trip");
        }

        // อัปเดต field ต่าง ๆ
        trip.setTitle(req.getTitle());
        trip.setDescription(req.getDescription());
        trip.setProvince(req.getProvince());
        trip.setTags(req.getTags());
        trip.setLatitude(req.getLatitude());
        trip.setLongitude(req.getLongitude());

        Trip saved = tripRepository.save(trip);
        return toResponse(saved);
    }

    /**
     * ลบทริป (DELETE /api/trips/{id})
     * ลบได้เฉพาะเจ้าของทริป
     */
    public void deleteTrip(Long id, User currentUser) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (trip.getAuthor() == null ||
                !trip.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not allowed to delete this trip");
        }

        tripRepository.delete(trip);
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