package com.travel.explorer.server.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TripMetaResponse {

    // รายชื่อจังหวัดทั้งหมด (ไม่ซ้ำ)
    private List<String> provinces;

    // รายชื่อแท็กทั้งหมด (ไม่ซ้ำ)
    private List<String> tags;
}
