package com.travel.explorer.server.repository;

import com.travel.explorer.server.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("""
        SELECT t
        FROM Trip t
        WHERE 
            (:keyword IS NULL OR :keyword = '')
            OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY t.createdAt DESC
    """)
    List<Trip> search(@Param("keyword") String keyword);
}