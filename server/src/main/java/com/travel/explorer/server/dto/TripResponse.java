package com.travel.explorer.server.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder         // 👈 ใช้ builder pattern สวยมาก
public class TripResponse {

    private Long id;
    private String title;
    private String description;
    private List<String> photos;
    private List<String> tags;
    private Double latitude;
    private Double longitude;
    private String province;
    private String authorName;
}