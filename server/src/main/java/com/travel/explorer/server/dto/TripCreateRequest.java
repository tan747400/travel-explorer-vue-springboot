package com.travel.explorer.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

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

    // ---------- fields ใหม่ ----------

    /**
     * tags เช่น ["ธรรมชาติ","ภูเขา","หน้าหนาว"]
     * ไม่บังคับส่งมา
     */
    private List<String> tags;

    /**
     * พิกัดละติจูด / ลองจิจูด
     * ไม่บังคับ ถ้าไม่รู้ส่ง null ได้
     */
    private Double latitude;
    private Double longitude;
}