package com.travel.explorer.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TripCreateRequest {

    @NotBlank(message = "title ห้ามว่าง")
    @Size(max = 255, message = "title ต้องไม่เกิน 255 ตัวอักษร")
    private String title;

    @NotBlank(message = "province ห้ามว่าง")
    @Size(max = 255, message = "province ต้องไม่เกิน 255 ตัวอักษร")
    private String province;

    @Size(max = 1000, message = "description ต้องไม่เกิน 1000 ตัวอักษร")
    private String description;
}