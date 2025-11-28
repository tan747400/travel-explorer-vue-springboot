package com.travel.explorer.server.controller;

import com.travel.explorer.server.entity.Trip;
import com.travel.explorer.server.repository.TripRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/share")
public class ShareController {

    private final TripRepository tripRepository;

    private static final String FRONTEND_BASE_URL =
            "https://travel-explorer-vue-springboot.vercel.app";

    private static final String DEFAULT_OG_IMAGE =
            "https://travel-explorer-vue-springboot.vercel.app/og-cover.png";

    public ShareController(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @GetMapping("/trips/{id}")
    public ResponseEntity<String> shareTrip(@PathVariable Long id) {

        Optional<Trip> optTrip = tripRepository.findById(id);
        if (optTrip.isEmpty()) {
            String home = FRONTEND_BASE_URL;
            String html404 = """
                <!doctype html>
                <html lang="th">
                  <head>
                    <meta charset="UTF-8" />
                    <title>ไม่พบทริป | Travel Explorer</title>
                    <meta http-equiv="refresh" content="0; url=%s" />
                  </head>
                  <body>
                    <p>ไม่พบทริป กำลังพากลับหน้าแรก…</p>
                    <script>window.location.href = '%s';</script>
                  </body>
                </html>
                """.formatted(home, home);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE,
                            MediaType.TEXT_HTML_VALUE + "; charset=UTF-8")
                    .body(html404);
        }

        Trip trip = optTrip.get();

        String title = safe(trip.getTitle(), "เที่ยวไหนดี");
        String desc = safe(trip.getDescription(), "เว็บไซต์รวมไอเดียทริปจากผู้ใช้งานจริง");

        String img = getFirstPhoto(trip);
        String canonical = FRONTEND_BASE_URL + "/trips/" + trip.getId();

        String html = buildShareHtml(title, desc, img, canonical);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        MediaType.TEXT_HTML_VALUE + "; charset=" + StandardCharsets.UTF_8)
                .body(html);
    }

    /* ===============================================================
       Helpers
    =============================================================== */

    private String getFirstPhoto(Trip t) {
        List<String> photos = t.getPhotos();
        if (photos != null && !photos.isEmpty()) {
            return photos.get(0);
        }
        return DEFAULT_OG_IMAGE;
    }

    private String safe(String val, String fallback) {
        if (val == null || val.isBlank()) return fallback;
        return val.replace("\"", "'");
    }

    private String buildShareHtml(String title,
                                  String desc,
                                  String img,
                                  String url) {

        return """
            <!doctype html>
            <html lang="th">
              <head>
                <meta charset="UTF-8" />

                <meta property="og:type" content="article" />
                <meta property="og:title" content="%s" />
                <meta property="og:description" content="%s" />
                <meta property="og:image" content="%s" />
                <meta property="og:url" content="%s" />
                <meta property="og:site_name" content="Travel Explorer" />

                <meta name="twitter:card" content="summary_large_image" />
                <meta name="twitter:title" content="%s" />
                <meta name="twitter:description" content="%s" />
                <meta name="twitter:image" content="%s" />

                <meta http-equiv="refresh" content="0; url=%s" />

                <title>%s - Travel Explorer</title>
              </head>

              <body>
                <p>กำลังเปลี่ยนเส้นทางไปยังหน้าทริป…</p>
                <script>
                  window.location.href = '%s';
                </script>
              </body>
            </html>
            """.formatted(
                title, desc, img, url,
                title, desc, img,
                url,
                title,
                url
        );
    }
}