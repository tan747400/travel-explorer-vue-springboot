package com.travel.explorer.server.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private static final String TRIP_FOLDER = "travel-explorer/trips";
    private static final String UPLOAD_MARKER = "/upload/";

    private final Cloudinary cloudinary;

    /**
     * อัปโหลดรูปไป Cloudinary และคืนค่า secure_url กลับมา
     */
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be null or empty");
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            // แยกโฟลเดอร์เก็บรูป
                            "folder", TRIP_FOLDER
                    )
            );

            Object secureUrlObj = uploadResult.get("secure_url");
            if (secureUrlObj == null) {
                throw new IllegalStateException("Cloudinary did not return secure_url");
            }

            // secure_url = URL แบบ HTTPS
            return secureUrlObj.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    /**
     * ลบรูปออกจาก Cloudinary ตาม URL ที่เก็บไว้ใน DB
     *
     * @param imageUrl secure_url จาก Cloudinary
     * @return true ถ้าลบสำเร็จ หรือรูปไม่เจออยู่แล้ว / false ถ้า URL ไม่ถูกต้อง
     */
    public boolean deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return false;
        }

        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId == null || publicId.isBlank()) {
                // URL ไม่ตรง format ของ Cloudinary
                return false;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );

            Object status = result.get("result");
            // Cloudinary จะคืนค่า "ok" ถ้าลบสำเร็จ, "not found" ถ้าไม่มีไฟล์นี้อยู่แล้ว
            if (status == null) return false;

            String s = status.toString();
            return "ok".equalsIgnoreCase(s) || "not found".equalsIgnoreCase(s);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    /**
     * ดึง public_id จาก secure_url ของ Cloudinary
     * ตัวอย่าง:
     *   https://res.cloudinary.com/<cloud>/image/upload/v123456789/travel-explorer/trips/abc123.jpg
     *   -> public_id = travel-explorer/trips/abc123
     */
    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            // เช่น /<cloud>/image/upload/v123456789/travel-explorer/trips/abc123.jpg
            String path = url.getPath();

            int idx = path.indexOf(UPLOAD_MARKER);
            if (idx < 0) {
                throw new IllegalArgumentException("Invalid Cloudinary URL format");
            }

            // ส่วนหลัง /upload/
            String afterUpload = path.substring(idx + UPLOAD_MARKER.length()); // v123456789/travel-explorer/trips/abc123.jpg

            String[] parts = afterUpload.split("/");
            int startIndex = 0;

            // ถ้าส่วนแรกเป็น version (v12345...) ให้ข้าม
            if (parts.length > 0 && parts[0].startsWith("v")) {
                String maybeNumber = parts[0].substring(1);
                if (!maybeNumber.isEmpty() && maybeNumber.chars().allMatch(Character::isDigit)) {
                    startIndex = 1;
                }
            }

            if (startIndex >= parts.length) {
                throw new IllegalArgumentException("Cannot extract public_id from URL");
            }

            StringBuilder sb = new StringBuilder();
            for (int i = startIndex; i < parts.length; i++) {
                sb.append(parts[i]);
                if (i < parts.length - 1) {
                    sb.append("/");
                }
            }

            // ตัดนามสกุลไฟล์ออก (.jpg, .png, ...)
            String withExt = sb.toString();
            int dotIdx = withExt.lastIndexOf('.');
            if (dotIdx > 0) {
                return withExt.substring(0, dotIdx);
            }

            return withExt;

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid image URL: " + imageUrl, e);
        }
    }
}