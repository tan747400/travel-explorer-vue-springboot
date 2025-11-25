package com.travel.explorer.server.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

    @NotBlank(message = "currentPassword is required")
    private String currentPassword;

    @NotBlank(message = "newPassword is required")
    @Size(
        min = 6,
        max = 100,
        message = "รหัสผ่านใหม่ต้องมีความยาวอย่างน้อย 6 และไม่เกิน 100 ตัวอักษร"
    )
    private String newPassword;
}