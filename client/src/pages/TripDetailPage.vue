<template>
  <div class="min-h-screen bg-slate-50">
    <div class="max-w-6xl mx-auto px-4 py-10">
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
      <div v-else>
        <!-- Header -->
        <header
          class="mb-6 flex flex-col md:flex-row md:items-end md:justify-between gap-3"
        >
          <div>
            <h1 class="text-3xl md:text-4xl font-bold mb-1">
              {{ trip.title }}
            </h1>

            <p class="text-sky-700 text-sm">
              {{ trip.province || "ไม่ระบุสถานที่" }}
            </p>

            <p class="text-xs text-gray-500 mt-1">
              สร้างโดย: {{ trip.authorName || "-" }}
            </p>
          </div>

          <div class="flex flex-col items-end gap-2">
            <!-- ปุ่มกลับ -->
            <button
              type="button"
              class="text-sm text-slate-600 hover:underline"
              @click="goBackSmart"
            >
              {{ backButtonLabel }}
            </button>

            <!-- ปุ่มจัดการทริป (เฉพาะเจ้าของเท่านั้น) -->
            <div
              v-if="isOwner"
              class="flex items-center gap-2"
            >
              <button
                type="button"
                class="px-3 py-1 rounded-md border border-amber-300 text-xs md:text-sm
                       text-amber-700 bg-amber-50 hover:bg-amber-100"
                @click="goEdit"
              >
                แก้ไขทริป
              </button>

              <button
                type="button"
                class="px-3 py-1 rounded-md border border-red-200 text-xs md:text-sm
                       text-red-600 hover:bg-red-50 disabled:opacity-60 disabled:cursor-not-allowed"
                :disabled="deleting"
                @click="openDeleteModal"
              >
                {{ deleting ? "กำลังลบ..." : "ลบทริป" }}
              </button>
            </div>
          </div>
        </header>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <!-- Photos -->
          <section class="lg:col-span-2 space-y-4">
            <!-- Main Image -->
            <div
              v-if="trip.photos && trip.photos.length > 0"
              class="aspect-[16/9] overflow-hidden rounded-2xl bg-slate-100"
            >
              <img
                :src="currentMainImage"
                :alt="trip.title"
                class="h-full w-full object-cover"
              />
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
              class="grid grid-cols-3 gap-2"
            >
              <button
                v-for="(p, idx) in trip.photos"
                :key="idx"
                type="button"
                class="relative group aspect-[4/3] w-full overflow-hidden rounded-lg bg-slate-100"
                @click="selectPhoto(idx)"
              >
                <img
                  :src="p"
                  :alt="`photo-${idx}`"
                  class="w-full h-full object-cover transition-opacity"
                  :class="idx === mainImageIndex
                    ? 'opacity-100'
                    : 'opacity-80 group-hover:opacity-100'"
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
            <h2 class="font-semibold mb-1">แผนที่</h2>

            <div
              v-if="hasLocation"
              class="rounded-xl overflow-hidden border bg-white"
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

              <div class="p-3 border-t text-right">
                <a
                  :href="mapExternalUrl"
                  target="_blank"
                  class="text-xs text-sky-600 hover:underline"
                >
                  เปิดใน Google Maps ↗
                </a>
              </div>
            </div>

            <p v-else class="text-sm text-gray-500">
              ยังไม่มีข้อมูลพิกัดของสถานที่นี้
            </p>
          </aside>
        </div>

        <!-- Description & Tags -->
        <section class="mt-8 space-y-3">
          <h2 class="font-semibold text-lg">รายละเอียดสถานที่</h2>
          <p class="text-sm text-gray-700 leading-relaxed">
            {{ trip.description || "ยังไม่มีรายละเอียดของทริปนี้" }}
          </p>

          <div
            v-if="trip.tags && trip.tags.length > 0"
            class="mt-4 flex flex-wrap gap-2"
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
      </div>
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
 * ข้อความบนปุ่มย้อนกลับ
 */
const backButtonLabel = computed(() => {
  if (fromDashboard.value) return "← กลับไป Dashboard";
  if (hasSearchQuery.value) return "← กลับไปผลการค้นหาเดิม";
  return "← กลับหน้าหลัก";
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
 * ปุ่มกลับแบบฉลาด
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

/* โหลดข้อมูลทริป (public – ไม่ใช้ token) */
async function loadTrip() {
  loading.value = true;
  error.value = "";

  try {
    const id = Number(route.params.id);
    trip.value = await getTripById(id);
    mainImageIndex.value = 0;
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

  // ✅ ถ้าผู้ใช้ "ล็อกอินอยู่" + token พัง/หมดอายุ → toast + เด้ง login
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