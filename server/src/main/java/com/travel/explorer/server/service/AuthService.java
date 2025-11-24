package com.travel.explorer.server.service;

import com.travel.explorer.server.entity.User;
import com.travel.explorer.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    // ถ้ามี BCryptPasswordEncoder เป็น @Bean อยู่แล้ว สามารถรับจาก constructor แทนได้
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * เปลี่ยนรหัสผ่านของ user โดยตรวจสอบรหัสผ่านเดิมก่อน
     *
     * @param user            ผู้ใช้ปัจจุบัน (ดึงจาก token แล้ว)
     * @param currentPassword รหัสผ่านเดิม
     * @param newPassword     รหัสผ่านใหม่
     */
    public void changePassword(User user, String currentPassword, String newPassword) {

        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("กรุณากรอกรหัสผ่านเดิม");
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("กรุณากรอกรหัสผ่านใหม่");
        }

        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("รหัสผ่านใหม่ต้องมีอย่างน้อย 6 ตัวอักษร");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("รหัสผ่านเดิมไม่ถูกต้อง");
        }

        // ตั้งรหัสผ่านใหม่
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}