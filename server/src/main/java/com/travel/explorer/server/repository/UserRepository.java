package com.travel.explorer.server.repository;

import com.travel.explorer.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // เช็ค email มีในระบบไหม (ใช้ตอน Register)
    boolean existsByEmail(String email);

    // ใช้ตอน Login
    Optional<User> findByEmail(String email);
}