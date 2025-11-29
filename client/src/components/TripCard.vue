<template>
  <article
    class="relative group flex flex-col md:flex-row items-start gap-4 p-4 md:p-5
           bg-white rounded-3xl border border-slate-200 shadow-sm
           hover:shadow-md hover:border-sky-200 transition-colors"
  >
    <!-- รูปหลัก -->
    <div
      class="relative w-full md:w-[320px] h-[220px] rounded-2xl overflow-hidden bg-slate-100 shrink-0"
    >
      <img
        v-if="item.photos?.[0]"
        :src="item.photos[0]"
        :alt="item.title"
        class="w-full h-full object-cover group-hover:scale-[1.02] transition-transform duration-300"
      />

      <div
        v-else
        class="w-full h-full flex items-center justify-center text-xs text-slate-400"
      >
        ยังไม่มีรูปภาพสำหรับทริปนี้
      </div>

      <!-- ป้ายจังหวัด -->
      <div
        v-if="item.province"
        class="absolute left-3 top-3 inline-flex items-center rounded-full
               bg-black/60 text-[11px] text-white px-2.5 py-0.5 backdrop-blur-sm"
      >
        <span class="truncate max-w-[150px]">
          {{ item.province }}
        </span>
      </div>
    </div>

    <!-- เนื้อหา -->
    <div class="flex flex-col flex-1 gap-1.5 pr-0 md:pr-20">
      <h2 class="mb-0.5">
        <RouterLink
          :to="detailLink"
          class="text-base sm:text-lg md:text-xl font-semibold text-slate-900
                 hover:text-sky-600 hover:underline line-clamp-2"
        >
          {{ item.title }}
        </RouterLink>
      </h2>

      <p class="text-xs text-slate-500 mb-1">
        <span v-if="item.authorName">
          สร้างโดย
          <span class="font-medium text-slate-700">{{ item.authorName }}</span>
        </span>
        <span v-else-if="item.province">
          สถานที่:
          <span class="font-medium text-slate-700">{{ item.province }}</span>
        </span>
      </p>

      <p class="text-sm text-slate-700 mb-1 line-clamp-3">
        {{ shortDesc }}
      </p>

      <RouterLink
        :to="detailLink"
        class="inline-flex text-xs sm:text-sm text-sky-600"
      >
        <span
          class="inline-flex items-center gap-1 border-b border-transparent pb-[1px]
                 hover:text-sky-700 hover:border-sky-600"
        >
          <span>ดูรายละเอียดทริป</span>
          <span>➜</span>
        </span>
      </RouterLink>

      <TagList
        class="mt-1"
        :tags="item.tags || []"
        :currentKeyword="keyword"
        @clickTag="handleClickTag"
      />

      <PhotoGrid
        class="mt-2"
        :photos="(item.photos || []).slice(1, 4)"
        :title="item.title"
      />

      <!-- ปุ่ม Share + Copy -->
      <div
        class="mt-3 flex justify-end gap-2.5
               md:mt-0 md:absolute md:right-4 md:bottom-4 md:h-11 md:items-center"
      >
        <!-- LINE -->
        <button
          type="button"
          class="share-btn h-11 w-11"
          @click="shareToLine"
          title="Share to LINE"
          aria-label="Share to LINE"
        >
          <span class="share-btn-inner">
            <Icon icon="ri:line-fill" class="h-6 w-6 text-[#06C755]" />
          </span>
        </button>

        <!-- X -->
        <button
          type="button"
          class="share-btn h-11 w-11"
          @click="shareToX"
          title="Share to X"
          aria-label="Share to X"
        >
          <span class="share-btn-inner">
            <Icon icon="ri:twitter-x-fill" class="h-6 w-6 text-slate-900" />
          </span>
        </button>

        <!-- Facebook -->
        <button
          type="button"
          class="share-btn h-11 w-11"
          @click="shareToFacebook"
          title="Share to Facebook"
          aria-label="Share to Facebook"
        >
          <span class="share-btn-inner">
            <Icon icon="ri:facebook-circle-fill" class="h-6 w-6 text-[#1877F2]" />
          </span>
        </button>

        <!-- Copy ลิงก์ (ใช้ลิงก์ share ที่มี OG ด้วย) -->
        <div class="h-11 w-11 flex.items-center.justify-center">
          <CopyButton :url="sharePageUrl" />
        </div>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { RouterLink, useRoute } from "vue-router";
import { Icon } from "@iconify/vue";

import TagList from "./TagList.vue";
import PhotoGrid from "./PhotoGrid.vue";
import CopyButton from "./CopyButton.vue";
import type { Trip } from "@/types/trip";

const props = defineProps<{
  item: Trip & { url?: string };
  keyword: string;
}>();

const emit = defineEmits<{
  (e: "addKeyword", tag: string): void;
}>();

const route = useRoute();

const item = props.item;
const keyword = props.keyword;

// base URL สำหรับ share-page (โดเมน frontend) จาก env
// ตัวนี้จะใช้แบบ: `${VITE_SHARE_BASE_URL}/trips/:id`
const SHARE_BASE = import.meta.env.VITE_SHARE_BASE_URL || "";

// คำบรรยายสั้น
const shortDesc = computed(() => {
  const text = (item.description ?? "").trim();
  return text.length > 120 ? text.slice(0, 120) + "…" : text;
});

// ใช้เป็น title / ข้อความแชร์
const shareTitle = computed(() => item.title || "เที่ยวไหนดี");
const shareText = computed(() => {
  const desc = shortDesc.value;
  return desc ? `${shareTitle.value} – ${desc}` : shareTitle.value;
});

// query ปัจจุบันของหน้า Home (ใช้ส่งติดไปหน้า detail เพื่อ back-to-search)
const searchQuery = computed(() => {
  const { keyword, province, tag } = route.query;
  const q: Record<string, any> = {};

  if (typeof keyword === "string" && keyword.trim()) q.keyword = keyword;
  if (typeof province === "string" && province) q.province = province;
  if (typeof tag === "string" && tag) q.tag = tag;

  return q;
});

// URL path สำหรับทริป (ฝั่ง frontend router)
const detailUrl = computed(() =>
  item.url ? item.url : `/trips/${item.id}`
);

// absolute URL ของหน้า detail (ใช้ fallback ถ้าไม่มี SHARE_BASE)
const absoluteDetailUrl = computed(() => {
  if (typeof window === "undefined") return detailUrl.value;
  return new URL(detailUrl.value, window.location.origin).toString();
});

// ลิงก์สำหรับ open graph: ชี้ไปที่ /share/trips/:id บนโดเมน frontend
const sharePageUrl = computed(() => {
  // ถ้าเซ็ต VITE_SHARE_BASE_URL แล้ว เช่น "https://travel-explorer-vue-springboot.vercel.app/share"
  if (SHARE_BASE) {
    const base = SHARE_BASE.replace(/\/+$/, "");
    return `${base}/trips/${item.id}`;
  }

  // ถ้าไม่ได้เซ็ต env: สร้างจาก window.location.origin + /share
  if (typeof window !== "undefined") {
    const base = `${window.location.origin.replace(/\/+$/, "")}/share`;
    return `${base}/trips/${item.id}`;
  }

  // fallback สุดท้าย ใช้ลิงก์หน้า detail ปกติ
  return absoluteDetailUrl.value;
});

const detailLink = computed(() => ({
  name: "trip-detail",
  params: { id: item.id },
  query: searchQuery.value,
}));

function handleClickTag(tag: string) {
  emit("addKeyword", tag);
}

function openShareWindow(url: string) {
  if (typeof window === "undefined") return;
  const win = window.open(url, "_blank", "noopener,noreferrer");
  if (!win) {
    alert("กรุณาอนุญาตให้เบราว์เซอร์เปิดหน้าต่างป๊อปอัปเพื่อใช้ปุ่มแชร์นะคะ");
  }
}

/* ====== ฟังก์ชันแชร์ ====== */

// LINE: ส่งทั้งข้อความ + ลิงก์ใน text เดียวกัน
function shareToLine() {
  const text =
    `${shareTitle.value}\n` +
    (shortDesc.value ? `${shortDesc.value}\n` : "") +
    `${sharePageUrl.value}`;

  const shareUrl = `https://line.me/R/msg/text/?${encodeURIComponent(text)}`;
  openShareWindow(shareUrl);
}

// Facebook: แนบลิงก์ + quote (ข้อความ)
function shareToFacebook() {
  const url = sharePageUrl.value;
  const quote = shareText.value;

  const shareUrl =
    "https://www.facebook.com/sharer/sharer.php" +
    "?u=" + encodeURIComponent(url) +
    "&quote=" + encodeURIComponent(quote);

  openShareWindow(shareUrl);
}

// X (Twitter เดิม): text + url แยกกัน
function shareToX() {
  const text = shareText.value;

  const shareUrl =
    "https://twitter.com/intent/tweet" +
    "?text=" + encodeURIComponent(text) +
    "&url=" + encodeURIComponent(sharePageUrl.value);

  openShareWindow(shareUrl);
}
</script>

<style scoped>
.share-btn {
  @apply rounded-full flex items-center justify-center cursor-pointer border-none bg-transparent relative transition-all duration-200 ease-out;
}

.share-btn-inner {
  @apply w-full h-full rounded-full flex items-center justify-center
         shadow-md border border-white/50 transition-all duration-200.ease-out
         bg-gradient-to-br from-sky-100 via-white to-sky-200;
  position: relative;
  overflow: hidden;
}

.share-btn::before {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: 9999px;
  background: radial-gradient(circle at 30% 0%, rgba(125, 211, 252, 0.45), transparent 60%),
              radial-gradient(circle at 70% 100%, rgba(59, 130, 246, 0.35), transparent 55%);
  opacity: 0;
  filter: blur(6px);
  transition: opacity 0.25s ease-out;
  pointer-events: none;
}

.share-btn-inner::after {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, transparent 0%, rgba(255,255,255,0.65) 45%, transparent 90%);
  transform: translateX(-120%);
  opacity: 0;
}

.share-btn:hover .share-btn-inner {
  transform: translateY(-3px) scale(1.06);
  @apply shadow-lg;
}

.share-btn:hover::before {
  opacity: 1;
}

.share-btn:hover .share-btn-inner::after {
  opacity: 1;
  animation: share-shine 0.85s ease-out forwards;
}

.share-btn:active .share-btn-inner {
  transform: translateY(0) scale(0.92);
  @apply shadow-sm;
}

@keyframes share-shine {
  0% {
    transform: translateX(-120%);
  }
  100% {
    transform: translateX(120%);
  }
}
</style>