package com.travel.explorer.server.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * ใช้ตอนแก้ไขทริป (PUT /api/trips/{id})
 * - ทุก field เป็น optional (ไม่กรอก = ไม่แก้ไข)
 */
@Data
public class TripUpdateRequest {

    @Size(max = 255, message = "title ต้องไม่เกิน 255 ตัวอักษร")
    private String title;

    @Size(max = 255, message = "place ต้องไม่เกิน 255 ตัวอักษร")
    private String province;   // ใช้เป็นสถานที่ / เมือง / ประเทศ ก็ได้

    @Size(max = 1000, message = "description ต้องไม่เกิน 1000 ตัวอักษร")
    private String description;

    /**
     * tags เช่น ["ธรรมชาติ","ภูเขา","หน้าหนาว"]
     */
    private List<String> tags;

    /**
     * พิกัด latitude / longitude (optional)
     */
    private Double latitude;
    private Double longitude;
}