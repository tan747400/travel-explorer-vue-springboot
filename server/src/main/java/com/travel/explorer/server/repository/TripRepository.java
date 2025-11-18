package com.travel.explorer.server.repository;

import com.travel.explorer.server.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    /**
     * ค้นหา trip จาก keyword
     * - title, description, province
     * - tags (ใน column text[] ด้วย)
     */
    @Query(value = """
        SELECT *
        FROM trips t
        WHERE 
            (:keyword IS NULL OR :keyword = '')
            OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(t.province) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR EXISTS (
                SELECT 1
                FROM unnest(t.tags) AS tag
                WHERE LOWER(tag) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
        ORDER BY t.id DESC
        """,
        nativeQuery = true)
    List<Trip> search(@Param("keyword") String keyword);
}