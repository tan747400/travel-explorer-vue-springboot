// client/api/share/trips/[id].ts
import type { VercelRequest, VercelResponse } from "@vercel/node";

const BACKEND_BASE_URL =
  process.env.BACKEND_BASE_URL ||
  "https://travel-explorer-backend-9xp5.onrender.com";

const FRONTEND_BASE_URL =
  process.env.FRONTEND_BASE_URL ||
  "https://travel-explorer-vue-springboot.vercel.app";

interface Trip {
  id: number;
  title: string;
  description: string | null;
  photos?: string[] | null;
  province?: string | null;
}

export default async function handler(req: VercelRequest, res: VercelResponse) {
  const { id } = req.query;

  if (!id || Array.isArray(id)) {
    res.status(400).send("Bad request");
    return;
  }

  try {
    // 1) ดึงข้อมูลทริปจาก backend
    const tripRes = await fetch(`${BACKEND_BASE_URL}/api/trips/${id}`);
    if (!tripRes.ok) {
      res.status(404).send("Trip not found");
      return;
    }

    const trip = (await tripRes.json()) as Trip;

    const title = trip.title || "Travel Explorer – Trip";

    const descRaw =
      (trip.description || "").replace(/\s+/g, " ").trim() ||
      "เที่ยวไหนดีกับ Travel Explorer";
    const description =
      descRaw.length > 150 ? descRaw.slice(0, 150) + "…" : descRaw;

    // รูป cover: ใช้รูปแรกของทริป ถ้าไม่มีใช้ fallback ที่ frontend
    const imageUrl =
      (trip.photos && trip.photos[0]) ||
      `${FRONTEND_BASE_URL}/og-default.png`;

    // URL จริงของหน้า /trips/:id บน Vercel
    const tripUrl = `${FRONTEND_BASE_URL}/trips/${trip.id}`;

    // 2) สร้าง HTML พร้อม OG tags + redirect ไปหน้า /trips/:id
    const html = `<!doctype html>
<html lang="th">
<head>
  <meta charset="utf-8" />
  <title>${escapeHtml(title)}</title>

  <!-- Open Graph -->
  <meta property="og:type" content="website" />
  <meta property="og:url" content="${tripUrl}" />
  <meta property="og:title" content="${escapeHtml(title)}" />
  <meta property="og:description" content="${escapeHtml(description)}" />
  <meta property="og:image" content="${imageUrl}" />

  <!-- Twitter Card -->
  <meta name="twitter:card" content="summary_large_image" />
  <meta name="twitter:title" content="${escapeHtml(title)}" />
  <meta name="twitter:description" content="${escapeHtml(description)}" />
  <meta name="twitter:image" content="${imageUrl}" />

  <meta name="viewport" content="width=device-width, initial-scale=1" />
</head>
<body>
  <p>Redirecting to trip page…</p>
  <script>
    // พอ browser โหลดเสร็จ ให้เด้งไปหน้า /trips/${trip.id} จริง ๆ
    window.location.href = ${JSON.stringify(tripUrl)};
  </script>
</body>
</html>`;

    res.setHeader("Content-Type", "text/html; charset=utf-8");
    res.status(200).send(html);
  } catch (err) {
    console.error("Error in share handler:", err);
    res.status(500).send("Internal Server Error");
  }
}

// helper กัน XSS ง่าย ๆ
function escapeHtml(str: string): string {
  return str
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}