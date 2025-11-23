<template>
  <!-- มีทริป -->
  <div class="max-w-6xl mx-auto px-4 py-10" v-if="trip">
    <!-- Header -->
    <header
      class="mb-6 flex flex-col md:flex-row md:items-end md:justify-between gap-2"
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

        <!-- Thumbnail Images -->
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
              class="w-full h-full object-contain object-center transition-opacity"
              :class="
                idx === mainImageIndex
                  ? 'opacity-100'
                  : 'opacity-80 group-hover:opacity-100'
              "
            />
            <span
              v-if="idx === mainImageIndex"
              class="pointer-events-none absolute inset-0 rounded-lg ring-2 ring-sky-500"
            ></span>
          </button>
        </div>
      </section>

      <!-- Map Section -->
      <aside class="space-y-3">
        <h2 class="font-semibold mb-1">แผนที่</h2>

        <div v-if="hasLocation" class="rounded-xl overflow-hidden border bg-white">
          <iframe
            :src="mapEmbedUrl"
            width="100%"
            height="260"
            style="border:0;"
            allowfullscreen
            loading="lazy"
          ></iframe>

          <div class="p-3 border-t text-right">
            <a
              :href="mapExternalUrl"
              target="_blank"
              class="text-xs text-sky-600 hover:underline"
            >
              View on Google Maps
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
      <p class="text-sm text-gray-700">
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

  <!-- Loading / Error / Not found -->
  <div class="max-w-6xl mx-auto px-4 py-10" v-else>
    <Loading v-if="loading" />

    <ErrorState
      v-else-if="error"
      :message="error"
    />

    <EmptyState
      v-else
      message="ไม่พบทริปที่คุณต้องการแสดง"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import type { Trip } from "@/types/trip";
import { getTripById } from "@/services/tripService";

// state components
import Loading from "@/components/state/Loading.vue";
import ErrorState from "@/components/state/ErrorState.vue";
import EmptyState from "@/components/state/EmptyState.vue";

// Toast
import { useToast } from "vue-toastification";
const toast = useToast();

const route = useRoute();

const trip = ref<Trip | null>(null);
const loading = ref(false);
const error = ref("");

const mainImageIndex = ref(0);

const currentMainImage = computed(() => {
  if (!trip.value?.photos?.length) return "";
  return trip.value.photos[mainImageIndex.value] ?? trip.value.photos[0];
});

const hasLocation = computed(
  () => trip.value?.latitude != null && trip.value?.longitude != null
);

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

function selectPhoto(idx: number) {
  if (!trip.value?.photos) return;
  if (idx < 0 || idx >= trip.value.photos.length) return;
  mainImageIndex.value = idx;
}

async function loadTrip() {
  loading.value = true;
  error.value = "";
  try {
    const id = Number(route.params.id);
    trip.value = await getTripById(id);
    mainImageIndex.value = 0;

    if (!trip.value) {
      error.value = "";
    }
  } catch (err: any) {
    console.error(err);
    const message = err.message || "โหลดข้อมูลทริปไม่สำเร็จ";
    error.value = message;
    toast.error(message);
  } finally {
    loading.value = false;
  }
}

onMounted(loadTrip);
</script>