package com.travel.explorer.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "trips")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ชื่อทริป
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    // รายละเอียด (เก็บเป็น text)
    @Column(name = "description", columnDefinition = "text")
    private String description;

    // รูปภาพ (ถ้ามี) เก็บเป็น text[] (URL ของรูป)
    @Column(name = "photos", columnDefinition = "text[]")
    private List<String> photos;

    // แท็กของทริป เช่น ["ธรรมชาติ", "ภูเขา"]
    @Column(name = "tags", columnDefinition = "text[]")
    private List<String> tags;

    // พิกัด
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // จังหวัด / สถานที่หลัก
    @Column(name = "province", length = 100, nullable = false)
    private String province;

    // เจ้าของทริป
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    // เวลา create (ใช้เรียงลำดับ)
    @Column(name = "created_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}