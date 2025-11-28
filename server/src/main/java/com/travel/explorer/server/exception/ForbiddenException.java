package com.travel.explorer.server.exception;

/**
 * ใช้โยนกรณี user ไม่มีสิทธิ์แก้ไข / ลบทริป
 * เช่น ไม่ใช่เจ้าของทริปนั้น
 */
public class ForbiddenException extends RuntimeException { 

    public ForbiddenException(String message) {
        super(message);
    }
}