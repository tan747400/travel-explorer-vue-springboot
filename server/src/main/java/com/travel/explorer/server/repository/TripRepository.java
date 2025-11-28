package com.travel.explorer.server.repository;

import com.travel.explorer.server.entity.Trip;
import com.travel.explorer.server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    /**
     * ค้นหา trip ด้วย keyword
     * - title
     * - description
     * - province
     * - tags (array)
     */
    @Query(
        value = """
            SELECT *
            FROM trips t
            WHERE
                (:keyword IS NULL OR :keyword = '')
                OR LOWER(t.title)       LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.province)    LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR EXISTS (
                    SELECT 1
                    FROM unnest(t.tags) tag
                    WHERE LOWER(tag) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            """,
        countQuery = """
            SELECT COUNT(*)
            FROM trips t
            WHERE
                (:keyword IS NULL OR :keyword = '')
                OR LOWER(t.title)       LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.province)    LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR EXISTS (
                    SELECT 1
                    FROM unnest(t.tags) tag
                    WHERE LOWER(tag) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            """,
        nativeQuery = true
    )
    Page<Trip> search(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    /**
     * ดึงรายการทริปของ user คนหนึ่ง
     * ใช้กับ Dashboard (/api/trips/mine)
     * เรียง id จากมากไปน้อย (ทริปที่สร้างล่าสุดอยู่บนสุด)
     */
    List<Trip> findByAuthorOrderByIdDesc(User author);
}