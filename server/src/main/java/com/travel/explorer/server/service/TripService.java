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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final ImageUploadService imageUploadService;   // <- อันนี้สำคัญ ต้องมี

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
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }

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
                // สร้างทริปใหม่ photos ยังเป็น null/ว่างได้
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

    /**
     * อัปโหลดรูปหลายรูปสำหรับทริปหนึ่ง ๆ
     * 1) เช็คสิทธิ์ว่าเป็นเจ้าของ
     * 2) อัปโหลดแต่ละไฟล์ขึ้น Cloudinary
     * 3) เอา URL ที่ได้ไป append ใส่ photos แล้ว save
     * 4) คืน TripResponse ล่าสุด
     */
    public TripResponse uploadTripPhotos(Long id,
                                         List<MultipartFile> files,
                                         User currentUser) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // เช็คสิทธิ์: ต้องเป็นเจ้าของทริป
        if (trip.getAuthor() == null ||
                !trip.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not allowed to upload photos for this trip");
        }

        // ดึง photos เดิม ถ้า null ให้สร้าง list ใหม่
        List<String> photos = trip.getPhotos();
        if (photos == null) {
            photos = new ArrayList<>();
        }

        // loop ทุกไฟล์ที่ส่งมา
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            // เรียก service อัปโหลดรูปขึ้น Cloudinary
            String url = imageUploadService.uploadImage(file);

            // ถ้าอัปโหลดสำเร็จ (มี url กลับมา) ก็เพิ่มเข้า list
            if (url != null && !url.isBlank()) {
                photos.add(url);
            }
        }

        trip.setPhotos(photos);
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
                .authorName(
                        t.getAuthor() != null
                                ? t.getAuthor().getDisplayName()
                                : "Unknown"
                )
                .build();
    }
}