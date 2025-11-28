package com.travel.explorer.server.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private Long userId;
    private String email;
    private String displayName;

    // เพิ่ม field ส่ง URL รูปโปรไฟล์ไปให้ frontend
    private String profileImageUrl;
}