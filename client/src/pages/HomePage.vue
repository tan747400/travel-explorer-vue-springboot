<template>
  <div class="min-h-screen bg-slate-50">
    <main class="px-4 sm:px-6 lg:px-10 py-8 max-w-6xl mx-auto">
      <!-- Heading -->
      <h1
        class="text-4xl sm:text-5xl lg:text-6xl text-center text-sky-500 font-extrabold tracking-tight mb-10"
      >
        เที่ยวไหนดี
      </h1>

      <!-- Search -->
      <div class="mb-6 w-full">
        <SearchBar
          v-model="keyword"
          label="ค้นหาที่เที่ยว"
          placeholder="หาที่เที่ยวแล้วไปกัน ..."
        />
      </div>

      <!-- Filters -->
      <section
        v-if="status === 'success'"
        class="mb-8 bg-white rounded-2xl border border-slate-200 shadow-sm px-3 sm:px-5 lg:px-6 py-4 sm:py-5 flex flex-col gap-4"
      >
        <!-- Province Filter -->
        <div class="flex items-center gap-3 text-xs sm:text-sm">
          <span class="text-gray-500 whitespace-nowrap">กรองตามสถานที่:</span>

          <div class="relative">
            <select
              v-model="selectedProvince"
              class="border border-slate-300 rounded-lg px-3 py-1.5 pr-10
                     bg-white text-xs sm:text-sm shadow-sm
                     focus:outline-none focus:ring-2 focus:ring-sky-400
                     appearance-none cursor-pointer"
            >
              <option value="">ทั้งหมด</option>
              <option v-for="prov in provinces" :key="prov" :value="prov">
                {{ prov }}
              </option>
            </select>

            <span
              class="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-base font-semibold text-black"
            >
              ▾
            </span>
          </div>
        </div>

        <!-- Tag Filter -->
        <div class="flex flex-col gap-2 w-full">
          <div class="flex flex-row items-center justify-between">
            <span class="text-xs sm:text-sm text-gray-500">แท็ก:</span>

            <button
              v-if="shouldShowToggle"
              @click="showAllTags = !showAllTags"
              class="text-[11px] sm:text-xs text-sky-600 hover:text-sky-700 hover:underline"
            >
              {{ showAllTags ? "ซ่อนแท็ก" : "ดูแท็กทั้งหมด" }}
            </button>
          </div>

          <!-- Responsive Tags -->
          <div
            class="flex flex-wrap gap-2 transition-all duration-200"
            :class="showAllTags ? '' : 'max-h-[34px] overflow-hidden'"
          >
            <!-- All -->
            <button
              type="button"
              class="px-3 py-1.5 rounded-full border text-[11px] sm:text-xs shadow-sm whitespace-nowrap"
              :class="selectedTag === '' ? 'bg-sky-600 text-white border-sky-600'
                                         : 'bg-white text-gray-700 border-gray-300 hover:bg-sky-50'"
              @click="selectedTag = ''"
            >
              ทั้งหมด
            </button>

            <!-- Tags Loop -->
            <button
              v-for="tag in tags"
              :key="tag"
              type="button"
              class="px-3 py-1.5 rounded-full border text-[11px] sm:text-xs shadow-sm whitespace-nowrap"
              :class="selectedTag === tag ? 'bg-sky-600 text-white border-sky-600'
                                          : 'bg-white text-gray-700 border-gray-300 hover:bg-sky-50'"
              @click="selectedTag = tag"
            >
              #{{ tag }}
            </button>
          </div>
        </div>
      </section>

      <!-- States -->
      <Loading v-if="status === 'loading'" />
      <ErrorState v-else-if="status === 'error'" :message="errorMessage" />

      <EmptyState
        v-else-if="status === 'success' && filteredTrips.length === 0"
        message="ยังไม่มีทริปที่ตรงกับเงื่อนไขการค้นหา ลองเปลี่ยนคำค้นหาหรือตัวกรองดูนะ"
      />

      <!-- Trip List -->
      <div v-else-if="status === 'success'" class="flex flex-col gap-6">
        <TripCard
          v-for="item in filteredTrips"
          :key="item.id"
          :item="item"
          :keyword="keyword"
          @addKeyword="handleAddKeyword"
        />

        <!-- Load More -->
        <div class="flex justify-center mt-6">
          <button
            v-if="!lastPage"
            class="px-5 py-2.5 rounded-full border border-sky-500 text-sky-600 text-sm font-medium
                   bg-white shadow-sm hover:bg-sky-50"
            @click="fetchTrips(false)"
          >
            Load more
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from "vue";
import SearchBar from "../components/SearchBar.vue";
import TripCard from "../components/TripCard.vue";
import Loading from "../components/state/Loading.vue";
import ErrorState from "../components/state/ErrorState.vue";
import EmptyState from "../components/state/EmptyState.vue";

import { getTrips, getTripsMeta } from "../services/tripService";
import type { Trip } from "../types/trip";
import { useDebouncedEffect } from "../composables/useDebouncedEffect";

type Status = "idle" | "loading" | "success" | "error";

const status = ref<Status>("idle");

const trips = ref<Trip[]>([]);
const provinces = ref<string[]>([]);
const tags = ref<string[]>([]);

const selectedProvince = ref("");
const selectedTag = ref("");
const keyword = ref("");

const page = ref(0);
const size = ref(6);
const lastPage = ref(false);

const showAllTags = ref(false);
const shouldShowToggle = computed(() => tags.value.length > 8);

const errorMessage = ref("");

// กันข้อมูลซ้ำเวลามีหลายหน้า
const loadedIds = new Set<number>();

/* โหลด meta (province, tag) */
async function loadMeta() {
  try {
    const meta = await getTripsMeta();
    provinces.value = meta.provinces;
    tags.value = meta.tags;
  } catch (e) {
    console.error(e);
  }
}

/* Filter trip ตาม province / tag */
const filteredTrips = computed(() => {
  return trips.value.filter((t) => {
    if (selectedProvince.value && t.province !== selectedProvince.value)
      return false;
    if (selectedTag.value && !(t.tags || []).includes(selectedTag.value))
      return false;
    return true;
  });
});

/* ดึง trip ทีละ page */
async function fetchTrips(reset = false) {
  try {
    if (reset) {
      status.value = "loading";
      page.value = 0;
      lastPage.value = false;
      trips.value = [];
      loadedIds.clear();
    }

    const result = await getTrips(keyword.value, page.value, size.value);

    // กัน id ซ้ำ
    const unique = result.content.filter((t: Trip) => {
      if (loadedIds.has(t.id)) return false;
      loadedIds.add(t.id);
      return true;
    });

    trips.value.push(...unique);
    lastPage.value = result.last;
    page.value = result.number + 1;

    status.value = "success";
  } catch (err: any) {
    status.value = "error";
    errorMessage.value = err.message || "โหลดข้อมูลล้มเหลว";
  }
}

/* โหลดทุกหน้าให้ครบก่อนเวลา filter ด้วย province / tag */
async function ensureAllTripsLoaded() {
  while (!lastPage.value) {
    await fetchTrips(false);
  }
}

/* ถ้าเปลี่ยน province หรือ tag ให้ไปโหลดหน้าที่เหลือให้ครบก่อน */
watch([selectedProvince, selectedTag], async ([p, t]) => {
  if (!p && !t) return;
  await ensureAllTripsLoaded();
});

/* keyword search – ยิงใหม่ & reset filter */
useDebouncedEffect(
  () => {
    selectedProvince.value = "";
    selectedTag.value = "";
    fetchTrips(true);
  },
  [keyword],
  300
);

function handleAddKeyword(tag: string) {
  keyword.value = tag;
}

/* initial load + บังคับเลื่อนขึ้นบนสุดให้เห็น navbar ทุกครั้งที่เข้า / รีเฟรช */
onMounted(async () => {
  // ปิด behavior scroll-restore ของ browser
  if ("scrollRestoration" in history) {
    history.scrollRestoration = "manual";
  }

  // รอให้ browser ทำงานรอบแรกเสร็จก่อน แล้วค่อยเลื่อนขึ้นบนสุด
  setTimeout(() => {
    window.scrollTo({ top: 0, left: 0, behavior: "auto" });
  }, 0);

  await loadMeta();
  await fetchTrips(true);
});
</script>

<style scoped></style>