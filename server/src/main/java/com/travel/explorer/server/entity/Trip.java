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

    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "text[]")
    private List<String> photos;

    @Column(columnDefinition = "text[]")
    private List<String> tags;

    private Double latitude;

    private Double longitude;

    @Column(length = 100)
    private String province;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    // 👇 เพิ่ม createdAt เพื่อ ORDER BY
    @Column(name = "created_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}