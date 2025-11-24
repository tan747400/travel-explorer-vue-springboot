package com.travel.explorer.server.service;

import com.travel.explorer.server.dto.TripCreateRequest;
import com.travel.explorer.server.dto.TripUpdateRequest;
import com.travel.explorer.server.dto.TripResponse;
import com.travel.explorer.server.dto.TripMetaResponse;
import com.travel.explorer.server.entity.Trip;
import com.travel.explorer.server.entity.User;
import com.travel.explorer.server.exception.ForbiddenException;
import com.travel.explorer.server.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final ImageUploadService imageUploadService;

    // ================================
    //  NEW: getTripsMeta()
    // ================================
    public TripMetaResponse getTripsMeta() {

        List<Trip> trips = tripRepository.findAll();

        Set<String> provSet = new HashSet<>();
        Set<String> tagSet = new HashSet<>();

        for (Trip t : trips) {
            if (t.getProvince() != null && !t.getProvince().isBlank()) {
                provSet.add(t.getProvince().trim());
            }

            if (t.getTags() != null) {
                t.getTags().forEach(tag -> {
                    if (tag != null && !tag.isBlank()) {
                        tagSet.add(tag.trim());
                    }
                });
            }
        }

        List<String> provinces = provSet.stream().sorted().collect(Collectors.toList());
        List<String> tags = tagSet.stream().sorted().collect(Collectors.toList());

        return TripMetaResponse.builder()
                .provinces(provinces)
                .tags(tags)
                .build();
    }

    // ================================
    //   GET /api/trips
    // ================================
    public Page<TripResponse> getTrips(String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Trip> tripPage;

        if (keyword == null || keyword.isBlank()) {
            tripPage = tripRepository.findAll(pageable);
        } else {
            tripPage = tripRepository.search(keyword.toLowerCase(), pageable);
        }

        return tripPage.map(this::toResponse);
    }

    public TripResponse getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        return toResponse(trip);
    }

    public List<TripResponse> getTripsOfUser(User user) {
        if (user == null) throw new IllegalArgumentException("User must not be null");

        List<Trip> trips = tripRepository.findByAuthorOrderByIdDesc(user);
        return trips.stream().map(this::toResponse).collect(Collectors.toList());
    }

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

    public TripResponse updateTrip(Long id, TripUpdateRequest req, User currentUser) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not allowed to edit this trip");
        }

        trip.setTitle(req.getTitle());
        trip.setDescription(req.getDescription());
        trip.setProvince(req.getProvince());
        trip.setTags(req.getTags());
        trip.setLatitude(req.getLatitude());
        trip.setLongitude(req.getLongitude());

        Trip saved = tripRepository.save(trip);
        return toResponse(saved);
    }

    public void deleteTrip(Long id, User currentUser) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not allowed to delete this trip");
        }

        tripRepository.delete(trip);
    }

    // ======================================
    //   Upload Trip Photos
    // ======================================
    public TripResponse uploadTripPhotos(Long id, List<MultipartFile> files, User currentUser) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not allowed to upload photos for this trip");
        }

        List<String> photos = trip.getPhotos();
        if (photos == null) photos = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            String url = imageUploadService.uploadImage(file);

            if (url != null && !url.isBlank()) {
                photos.add(url);
            }
        }

        trip.setPhotos(photos);
        Trip saved = tripRepository.save(trip);

        return toResponse(saved);
    }

    // ======================================
    //   NEW: Delete Photo from Trip
    // ======================================
    public TripResponse deleteTripPhoto(Long id, String photoUrl, User currentUser) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not allowed to delete photos of this trip");
        }

        List<String> photos = trip.getPhotos();
        if (photos == null || photos.isEmpty()) {
            throw new RuntimeException("No photos to delete");
        }

        // ลบจาก Storage
        boolean deleted = imageUploadService.deleteImage(photoUrl);
        if (!deleted) {
            throw new RuntimeException("Failed to delete image from storage");
        }

        // ลบออกจาก List
        photos.removeIf(url -> url.equals(photoUrl));
        trip.setPhotos(photos);

        Trip saved = tripRepository.save(trip);

        return toResponse(saved);
    }

    // ======================================
    //   Convert Entity → Response
    // ======================================
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