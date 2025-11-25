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
    private final BCryptPasswordEncoder passwordEncoder;  // รับจาก Spring container

    /**
     * เปลี่ยนรหัสผ่านโดยตรวจสอบรหัสผ่านเดิมก่อน
     */
    public void changePassword(User user, String currentPassword, String newPassword) {

        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("กรุณากรอกรหัสผ่านเดิม");
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("กรุณากรอกรหัสผ่านใหม่");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("รหัสผ่านเดิมไม่ถูกต้อง");
        }

        if (currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException("รหัสผ่านใหม่ต้องไม่เหมือนกับรหัสผ่านเดิม");
        }

        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("รหัสผ่านใหม่ต้องมีอย่างน้อย 6 ตัวอักษร");
        }

        // บันทึกรหัสผ่านใหม่
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}