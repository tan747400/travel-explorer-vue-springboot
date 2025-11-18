package com.travel.explorer.server.dto;

import com.travel.explorer.server.entity.Trip;

import java.util.Arrays;
import java.util.List;

public record TripResponse(
        Long id,
        String title,
        String description,
        List<String> photos,
        List<String> tags,
        Double latitude,
        Double longitude,
        String authorName
) {
    public static TripResponse fromEntity(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getTitle(),
                trip.getDescription(),
                trip.getPhotos() != null ? Arrays.asList(trip.getPhotos()) : List.of(),
                trip.getTags() != null ? Arrays.asList(trip.getTags()) : List.of(),
                trip.getLatitude(),
                trip.getLongitude(),
                trip.getAuthor() != null ? trip.getAuthor().getDisplayName() : null
        );
    }
}
