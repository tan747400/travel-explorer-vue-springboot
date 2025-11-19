package com.travel.explorer.server.repository;

import com.travel.explorer.server.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TripRepository extends JpaRepository<Trip, Long> {

    /**
     * ค้นหา trip ด้วย keyword (title / description / province)
     * คืนเป็น Page เพื่อใช้ pagination
     */
    @Query(
        value = """
            SELECT t
            FROM Trip t
            WHERE
                (:keyword IS NULL OR :keyword = '')
                OR LOWER(t.title)       LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.province)    LIKE LOWER(CONCAT('%', :keyword, '%'))
            """,
        countQuery = """
            SELECT COUNT(t)
            FROM Trip t
            WHERE
                (:keyword IS NULL OR :keyword = '')
                OR LOWER(t.title)       LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.province)    LIKE LOWER(CONCAT('%', :keyword, '%'))
            """
    )
    Page<Trip> search(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}