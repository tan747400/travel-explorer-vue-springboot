<template>
  <div class="min-h-screen bg-slate-50">
    <div class="max-w-6xl mx-auto px-4 py-8 sm:py-10">
      <!-- Loading: ใช้ Skeleton -->
      <TripDetailSkeleton v-if="loading" />

      <!-- Error -->
      <ErrorState v-else-if="error" :message="error" />

      <!-- ไม่พบ -->
      <EmptyState
        v-else-if="!trip"
        message="ไม่พบทริปที่คุณต้องการแสดง"
      />

      <!-- Content -->
      <section
        v-else
        class="rounded-3xl bg-white border border-slate-200 shadow-sm px-4 sm:px-6 md:px-8 py-6 sm:py-8 md:py-9 space-y-6"
      >
        <!-- Header -->
        <header
          class="flex flex-col gap-4 border-b border-slate-100 pb-4 sm:pb-5"
        >
          <div
            class="flex flex-col md:flex-row md:items-start md:justify-between gap-4"
          >
            <!-- ซ้าย: ชื่อทริป -->
            <div class="space-y-2">
              <div class="flex flex-wrap items-center gap-2">
                <span
                  v-if="trip.province"
                  class="inline-flex items-center rounded-full bg-sky-50 text-sky-700 text-[11px] px-3 py-1 border border-sky-100"
                >
                  📍 {{ trip.province }}
                </span>
              </div>

              <h1
                class="text-2xl sm:text-3xl md:text-4xl font-bold text-slate-900"
              >
                {{ trip.title }}
              </h1>

              <div class="space-y-0.5 text-xs sm:text-sm">
                <p class="text-slate-500">
                  สร้างโดย
                  <span class="font-medium text-slate-700">
                    {{ trip.authorName || "-" }}
                  </span>
                </p>
              </div>
            </div>

            <!-- ขวา: ปุ่มย้อนกลับ + ปุ่มจัดการ -->
            <div class="flex flex-col items-end gap-2">
              <!-- ปุ่มกลับ -->
              <button
                type="button"
                class="inline-flex items-center gap-1 text-xs sm:text-sm text-sky-600
                       border-b border-transparent pb-[1px]
                       hover:text-sky-700 hover:border-sky-700"
                @click="goBackSmart"
              >
                <span>←</span>
                <span>{{ backButtonText }}</span>
              </button>

              <!-- ปุ่มจัดการทริป (เฉพาะเจ้าของเท่านั้น) -->
              <div
                v-if="isOwner"
                class="flex items-center gap-2"
              >
                <button
                  type="button"
                  class="px-3 py-1.5 rounded-lg border border-amber-300 text-xs md:text-sm
                         text-amber-700 bg-amber-50 hover:bg-amber-100 transition-colors"
                  @click="goEdit"
                >
                  แก้ไขทริป
                </button>

                <button
                  type="button"
                  class="px-3 py-1.5 rounded-lg border border-red-200 text-xs md:text-sm
                         text-red-600 hover:bg-red-50 disabled:opacity-60 disabled:cursor-not-allowed transition-colors"
                  :disabled="deleting"
                  @click="openDeleteModal"
                >
                  {{ deleting ? "กำลังลบ..." : "ลบทริป" }}
                </button>
              </div>
            </div>
          </div>
        </header>

        <!-- รูป + แผนที่ -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <!-- Photos -->
          <section class="lg:col-span-2 space-y-3">
            <!-- Main Image -->
            <div
              v-if="trip.photos && trip.photos.length > 0"
              class="relative aspect-[16/9] overflow-hidden rounded-2xl bg-slate-100"
            >
              <img
                :src="currentMainImage"
                :alt="trip.title"
                class="h-full w-full object-cover"
              />
              <div
                class="absolute bottom-3 right-3 rounded-full bg-black/40 text-[11px] text-white px-2.5 py-1 backdrop-blur-sm"
              >
                รูปที่ {{ mainImageIndex + 1 }} / {{ trip.photos.length }}
              </div>
            </div>
            <div
              v-else
              class="aspect-[16/9] rounded-2xl bg-slate-100 flex items-center justify-center text-gray-400 text-sm"
            >
              ยังไม่มีรูปภาพสำหรับทริปนี้
            </div>

            <!-- Thumbnails -->
            <div
              v-if="trip.photos && trip.photos.length > 1"
              class="flex gap-2 overflow-x-auto pb-1"
            >
              <button
                v-for="(p, idx) in trip.photos"
                :key="idx"
                type="button"
                class="relative group h-20 w-28 sm:h-24 sm:w-32 flex-shrink-0 overflow-hidden rounded-lg bg-slate-100"
                @click="selectPhoto(idx)"
              >
                <img
                  :src="p"
                  :alt="`photo-${idx}`"
                  class="w-full h-full object-cover transition-opacity"
                  :class="
                    idx === mainImageIndex
                      ? 'opacity-100'
                      : 'opacity-70 group-hover:opacity-100'
                  "
                />
                <span
                  v-if="idx === mainImageIndex"
                  class="pointer-events-none absolute inset-0 rounded-lg ring-2 ring-sky-500"
                ></span>
              </button>
            </div>
          </section>

          <!-- Map -->
          <aside class="space-y-3">
            <h2 class="font-semibold text-base sm:text-lg text-slate-900">
              แผนที่ & การเดินทาง
            </h2>

            <div
              v-if="hasLocation"
              class="rounded-2xl overflow-hidden border border-slate-200 bg-white shadow-sm"
            >
              <iframe
                :src="mapEmbedUrl"
                width="100%"
                height="260"
                style="border:0;"
                allowfullscreen
                loading="lazy"
                referrerpolicy="no-referrer-when-downgrade"
              ></iframe>

              <div class="p-3 border-t border-slate-100 text-right">
                <a
                  :href="mapExternalUrl"
                  target="_blank"
                  class="inline-flex items-center gap-1 text-xs text-sky-600
                         border-b border-transparent pb-[1px]
                         hover:text-sky-700 hover:border-sky-700"
                >
                  <span>เปิดใน Google Maps</span>
                  <span class="text-[10px]">↗</span>
                </a>
              </div>
            </div>

            <p v-else class="text-sm text-gray-500">
              ยังไม่มีข้อมูลพิกัดของสถานที่นี้
            </p>
          </aside>
        </div>

        <!-- Description & Tags -->
        <section class="pt-4 border-t border-slate-100 space-y-4">
          <h2 class="font-semibold text-lg text-slate-900">
            รายละเอียดสถานที่
          </h2>
          <p class="text-sm text-gray-700 leading-relaxed whitespace-pre-line">
            {{ trip.description || "ยังไม่มีรายละเอียดของทริปนี้" }}
          </p>

          <div
            v-if="trip.tags && trip.tags.length > 0"
            class="flex flex-wrap gap-2"
          >
            <span
              v-for="tag in trip.tags"
              :key="tag"
              class="inline-flex items-center rounded-full border border-sky-200 bg-sky-50 px-3 py-1 text-xs text-sky-700"
            >
              #{{ tag }}
            </span>
          </div>
        </section>
      </section>
    </div>

    <!-- Popup ลบทริป -->
    <Transition name="fade">
      <div
        v-if="showDeleteModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/30 backdrop-blur-sm"
      >
        <div class="w-full max-w-sm rounded-2xl bg-white p-6 shadow-xl">
          <h3 class="text-lg font-semibold text-slate-800 text-center">
            ต้องการลบทริปนี้จริง ๆ ไหม?
          </h3>
          <p class="mt-2 text-sm text-slate-500 text-center">
            การลบจะไม่สามารถกู้คืนได้
          </p>

          <div class="mt-6 flex items-center justify-center gap-3">
            <button
              type="button"
              class="px-4 py-2 rounded-lg border text-slate-600 hover:bg-slate-50"
              @click="closeDeleteModal"
              :disabled="deleting"
            >
              ยกเลิก
            </button>

            <button
              type="button"
              class="px-4 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700 disabled:opacity-60 disabled:cursor-not-allowed"
              :disabled="deleting"
              @click="confirmDeleteModal"
            >
              ลบทริป
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

import type { Trip } from "@/types/trip";
import { getTripById, deleteTrip } from "@/services/tripService";

import TripDetailSkeleton from "@/components/state/TripDetailSkeleton.vue";
import ErrorState from "@/components/state/ErrorState.vue";
import EmptyState from "@/components/state/EmptyState.vue";

import { useAuthStore } from "@/stores/authStore";
import { useToast } from "vue-toastification";
import { useSessionExpired } from "@/composables/useSessionExpired";

const toast = useToast();
const auth = useAuthStore();
const route = useRoute();
const router = useRouter();
const { handleSessionExpired } = useSessionExpired();

const trip = ref<Trip | null>(null);
const loading = ref(false);
const error = ref("");

const mainImageIndex = ref(0);
const deleting = ref(false);

// popup ลบ
const showDeleteModal = ref(false);

/* helper: ตรวจ token ผิด / หมดอายุไหม */
function isTokenInvalidOrExpired(token: string | null | undefined): boolean {
  if (!token) return true;

  try {
    const parts = token.split(".");
    if (parts.length !== 3) return true;

    const payloadPart = parts[1];
    if (!payloadPart) return true;

    const payloadBase64 = payloadPart.replace(/-/g, "+").replace(/_/g, "/");
    const json = atob(payloadBase64);
    const payload = JSON.parse(json) as { exp?: number };

    if (!payload.exp) {
      // ไม่มี exp ให้ไปลุ้น 401 จาก backend แทน
      return false;
    }

    const now = Math.floor(Date.now() / 1000);
    return payload.exp < now;
  } catch {
    return true;
  }
}

/**
 * มาจาก Dashboard ไหม? ดูจาก query ?from=dashboard
 */
const fromDashboard = computed(() => route.query.from === "dashboard");

/**
 * query สำหรับ back-to-search (เก็บแค่แบบ string ที่จำเป็น)
 */
const searchQueryForBack = computed(() => {
  const { keyword, province, tag } = route.query;
  const q: Record<string, string> = {};

  if (typeof keyword === "string" && keyword.trim()) {
    q.keyword = keyword.trim();
  }
  if (typeof province === "string" && province) {
    q.province = province;
  }
  if (typeof tag === "string" && tag) {
    q.tag = tag;
  }

  return q;
});

// มี query การค้นหามั้ย (ใช้ตัดสินใจข้อความปุ่มย้อนกลับ)
const hasSearchQuery = computed(
  () => Object.keys(searchQueryForBack.value).length > 0
);

/**
 * ข้อความบนปุ่มย้อนกลับ (ไม่ใส่ลูกศร ให้ template ใส่เอง)
 */
const backButtonText = computed(() => {
  if (fromDashboard.value) return "กลับไป Dashboard";
  if (hasSearchQuery.value) return "กลับไปผลการค้นหาเดิม";
  return "กลับหน้าหลัก";
});

const currentMainImage = computed(() => {
  if (!trip.value?.photos?.length) return "";
  return trip.value.photos[mainImageIndex.value] ?? trip.value.photos[0];
});

const hasLocation = computed(() => {
  if (!trip.value) return false;
  const lat = trip.value.latitude;
  const lng = trip.value.longitude;
  return (
    lat !== null &&
    lng !== null &&
    lat !== undefined &&
    lng !== undefined &&
    !Number.isNaN(Number(lat)) &&
    !Number.isNaN(Number(lng))
  );
});

const mapEmbedUrl = computed(() => {
  if (!hasLocation.value || !trip.value) return "";
  const { latitude, longitude } = trip.value;
  return `https://www.google.com/maps?q=${latitude},${longitude}&z=14&output=embed`;
});

const mapExternalUrl = computed(() => {
  if (!hasLocation.value || !trip.value) return "#";
  const { latitude, longitude } = trip.value;
  return `https://www.google.com/maps?q=${latitude},${longitude}`;
});

/* เป็นเจ้าของทริปไหม */
const isOwner = computed(() => {
  if (!trip.value || !auth.user) return false;

  const authorId = (trip.value as any).authorId;
  const userId = auth.user.userId;

  const byId = typeof authorId === "number" && authorId === userId;

  const byName =
    trip.value.authorName &&
    auth.user.displayName &&
    trip.value.authorName === auth.user.displayName;

  return byId || byName;
});

function selectPhoto(idx: number) {
  if (!trip.value?.photos) return;
  mainImageIndex.value = idx;
}

/**
 * ปุ่มกลับ
 */
function goBackSmart() {
  if (fromDashboard.value) {
    router.push({ name: "dashboard" });
    return;
  }

  if (hasSearchQuery.value) {
    router.push({
      name: "home",
      query: searchQueryForBack.value,
    });
    return;
  }

  if (window.history.length > 1) {
    router.back();
  } else {
    router.push({ name: "home" });
  }
}

function goEdit() {
  if (!trip.value) return;
  router.push({ name: "trip-edit", params: { id: trip.value.id } });
}

/* popup ลบ */
function openDeleteModal() {
  if (!isOwner.value) return;
  showDeleteModal.value = true;
}

function closeDeleteModal() {
  if (deleting.value) return;
  showDeleteModal.value = false;
}

/* ลบทริป */
async function confirmDeleteModal() {
  if (!trip.value) return;

  // ลบได้เฉพาะตอนที่ล็อกอิน + token ยังดีอยู่
  if (auth.isLoggedIn && isTokenInvalidOrExpired(auth.token)) {
    error.value = "เซสชั่นหมดอายุ กรุณาเข้าสู่ระบบใหม่อีกครั้ง";
    handleSessionExpired();
    return;
  }

  try {
    deleting.value = true;
    await deleteTrip(trip.value.id, auth.token);
    toast.success("ลบทริปสำเร็จแล้ว 🗑️");

    if (fromDashboard.value) {
      router.push({ name: "dashboard" });
    } else {
      router.push({ name: "home" });
    }
  } catch (err: any) {
    console.error(err);

    if (err?.response?.status === 401) {
      handleSessionExpired();
      return;
    }
    if (err?.response?.status === 403) {
      toast.error("คุณไม่สามารถลบทริปของคนอื่นได้");
      return;
    }

    toast.error(
      err?.response?.data?.message || err.message || "ลบทริปไม่สำเร็จ"
    );
  } finally {
    deleting.value = false;
    closeDeleteModal();
  }
}

/* ---------- อัปเดต meta สำหรับแชร์ (Facebook / X / LINE) ---------- */
function updateShareMeta(trip: Trip) {
  if (typeof window === "undefined" || typeof document === "undefined") return;

  const baseTitle = trip.title || "ทริปท่องเที่ยว";
  const siteTitle = `${baseTitle} | Travel Explorer`;

  const fullDesc =
    (trip.description && trip.description.trim()) ||
    "ทริปท่องเที่ยวจาก Travel Explorer";
  const description =
    fullDesc.length > 160 ? fullDesc.slice(0, 160) + "…" : fullDesc;

  const origin = window.location.origin;
  const url = new URL(`/trips/${trip.id}`, origin).toString();

  let image = "";
  if (trip.photos && trip.photos[0]) {
    image = new URL(trip.photos[0], origin).toString();
  }

  // ตั้ง title แท็บ
  document.title = siteTitle;

  const setMeta = (attr: "name" | "property", key: string, content: string) => {
    if (!content) return;
    let el = document.head.querySelector<HTMLMetaElement>(
      `meta[${attr}="${key}"]`
    );
    if (!el) {
      el = document.createElement("meta");
      el.setAttribute(attr, key);
      document.head.appendChild(el);
    }
    el.setAttribute("content", content);
  };

  // Description ทั่วไป
  setMeta("name", "description", description);

  // Open Graph
  setMeta("property", "og:title", siteTitle);
  setMeta("property", "og:description", description);
  setMeta("property", "og:type", "article");
  setMeta("property", "og:url", url);
  if (image) setMeta("property", "og:image", image);

  // Twitter
  setMeta("name", "twitter:card", "summary_large_image");
  setMeta("name", "twitter:title", siteTitle);
  setMeta("name", "twitter:description", description);
  if (image) setMeta("name", "twitter:image", image);
}

/* โหลดข้อมูลทริป (public – ไม่ใช้ token) */
async function loadTrip() {
  loading.value = true;
  error.value = "";

  try {
    const id = Number(route.params.id);
    const data = await getTripById(id);
    trip.value = data;
    mainImageIndex.value = 0;

    // อัปเดต meta สำหรับแชร์
    updateShareMeta(data);
  } catch (err: any) {
    console.error(err);
    error.value =
      err?.response?.data?.message || err.message || "โหลดข้อมูลทริปไม่สำเร็จ";
    toast.error(error.value);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  window.scrollTo({ top: 0, behavior: "auto" });

  // ถ้าผู้ใช้ "ล็อกอินอยู่" + token พัง/หมดอายุ → toast + เด้ง login
  if (auth.isLoggedIn && isTokenInvalidOrExpired(auth.token)) {
    handleSessionExpired();
    return;
  }

  // ถ้าไม่ได้ login หรือ token ยังดี → แค่โหลดทริปเฉย ๆ
  loadTrip();
});
</script>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>