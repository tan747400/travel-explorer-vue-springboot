<template>
  <div class="min-h-screen bg-gradient-to-b from-sky-50 via-slate-50 to-slate-100">
    <main class="px-4 sm:px-6 lg:px-10 py-8 sm:py-10 max-w-6xl mx-auto">
      <!-- Hero / Heading -->
      <header class="mb-6 sm:mb-8">
        <div
          class="rounded-3xl bg-gradient-to-r from-sky-500 via-sky-600 to-sky-700
                 text-white px-6 py-7 sm:px-8 sm:py-8 shadow-sm
                 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4"
        >
          <div class="space-y-3 max-w-xl">
            <div class="inline-flex items-center gap-2 rounded-full bg-white/10 px-3 py-1">
              <span class="h-1.5 w-1.5 rounded-full bg-emerald-300"></span>
              <span class="text-[11px] sm:text-xs font-medium tracking-wide">
                Travel ideas from real users
              </span>
            </div>

            <h1
              class="text-3xl sm:text-4xl lg:text-5xl font-extrabold tracking-tight"
            >
              เที่ยวไหนดี
            </h1>

            <p class="text-xs sm:text-sm text-sky-50/90 leading-relaxed">
              รวมไอเดียสถานที่เที่ยวและทริปในฝันจากผู้ใช้คนอื่น ๆ
              ค้นหาแรงบันดาลใจ แล้วเก็บไว้เป็นทริปของคุณเองได้ในไม่กี่คลิก
            </p>

            <div class="flex flex-wrap gap-2 pt-1">
              <span
                class="inline-flex items-center rounded-full bg-white/10 px-3 py-1
                       text-[11px] sm:text-xs font-medium"
              >
                🌏 กรองทริปได้ตามสถานที่
              </span>
              <span
                class="inline-flex items-center rounded-full bg-white/10 px-3 py-1
                       text-[11px] sm:text-xs font-medium"
              >
                # แท็กช่วยให้เจอทริปสไตล์ที่คุณชอบเร็วขึ้น
              </span>
            </div>
          </div>

          <div
            class="hidden sm:flex items-center justify-center flex-shrink-0"
          >
            <div
              class="h-20 w-20 sm:h-24 sm:w-24 rounded-full bg-white/10 border border-white/30
                     flex items-center justify-center shadow-inner"
            >
              <span class="text-3xl sm:text-4xl">✈️</span>
            </div>
          </div>
        </div>
      </header>

      <!-- Search box -->
      <section class="-mt-4 sm:-mt-6 mb-6 sm:mb-8">
        <div
          class="rounded-2xl bg-white/90 backdrop-blur border border-slate-200 shadow-sm
                 px-4 py-3 sm:px-5 sm:py-4 transition-shadow hover:shadow-md"
        >
          <SearchBar
            v-model="keyword"
            label="ค้นหาที่เที่ยว"
            placeholder="หาที่เที่ยวแล้วไปกัน ..."
          />
          <div
            class="mt-2 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-1"
          >
            <p class="text-[11px] sm:text-xs text-slate-400">
              แนะนำ: ลองค้นด้วยชื่อจังหวัด, ชื่อสถานที่, หรือแท็ก เช่น เชียงใหม่ ทะเล คาเฟ่
            </p>
          </div>
        </div>
      </section>

      <!-- ===== Loading: full-page skeleton สำหรับการโหลดครั้งแรก ===== -->
      <HomeSkeleton
        v-if="status === 'loading' && !hasLoadedOnce"
      />

      <!-- ===== Main content (โหลดสำเร็จอย่างน้อย 1 ครั้งแล้ว) ===== -->
      <template v-else>
        <!-- Filters -->
        <section
          v-if="status === 'success' || hasLoadedOnce"
          class="mb-8 rounded-2xl bg-white/90 backdrop-blur border border-slate-200 shadow-sm
                 px-4 sm:px-6 py-4 sm:py-5 space-y-4"
        >
          <!-- แถวบน: Province filter -->
          <div
            class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3"
          >
            <div class="flex items-center gap-2 text-xs sm:text-sm">
              <span class="text-slate-500 whitespace-nowrap flex items-center gap-1">
                <span class="text-base">📍</span>
                <span>กรองตามสถานที่</span>
              </span>

              <div class="relative inline-block">
                <select
                  v-model="selectedProvince"
                  class="border border-slate-300 rounded-lg px-3 py-1.5 pr-9
                         bg-white text-xs sm:text-sm shadow-sm
                         focus:outline-none focus:ring-2 focus:ring-sky-400
                         appearance-none cursor-pointer"
                >
                  <option value="">ทั้งหมด</option>
                  <option
                    v-for="prov in provinces"
                    :key="prov"
                    :value="prov"
                  >
                    {{ prov }}
                  </option>
                </select>

                <!-- arrow -->
                <span
                  class="pointer-events-none absolute right-2.5 top-1/2 -translate-y-1/2
                         text-sm font-semibold text-slate-500"
                >
                  ▾
                </span>
              </div>
            </div>

            <p class="text-[11px] sm:text-xs text-slate-400">
              เลือกจังหวัด + แท็กด้านล่าง เพื่อให้ผลลัพธ์แคบลง
            </p>
          </div>

          <!-- เส้นแบ่ง -->
          <div class="border-t border-slate-100"></div>

          <!-- แถวล่าง: Tags -->
          <div class="flex flex-col gap-2 w-full">
            <div class="flex justify-between items-center">
              <span class="text-xs sm:text-sm text-slate-500 flex items-center gap-1">
                <span class="text-base">🏷️</span>
                <span>แท็ก</span>
              </span>

              <button
                v-if="shouldShowToggle"
                @click="showAllTags = !showAllTags"
                class="text-[11px] sm:text-xs text-sky-600 hover:text-sky-700
                       border-b border-transparent pb-[1px]
                       hover:border-sky-700"
              >
                {{ showAllTags ? "ซ่อนแท็ก" : "ดูแท็กทั้งหมด" }}
              </button>
            </div>

            <div
              class="flex flex-wrap gap-2 transition-all duration-200"
              :class="showAllTags ? '' : 'max-h-[34px] overflow-hidden'"
            >
              <!-- All -->
              <button
                type="button"
                class="px-3 py-1.5 rounded-full border text-[12px] sm:text-xs shadow-sm whitespace-nowrap"
                :class="
                  selectedTag === ''
                    ? 'bg-sky-600 text-white border-sky-600'
                    : 'bg-white text-gray-700 border-gray-300 hover:bg-sky-50'
                "
                @click="handleSelectAllTag"
              >
                ทั้งหมด
              </button>

              <!-- Tag list -->
              <button
                v-for="tag in tags"
                :key="tag"
                type="button"
                class="px-3 py-1.5 rounded-full border text-[11px] sm:text-xs shadow-sm whitespace-nowrap"
                :class="
                  selectedTag === tag
                    ? 'bg-sky-600 text-white border-sky-600'
                    : 'bg-white text-gray-700 border-gray-300 hover:bg-sky-50'
                "
                @click="handleSelectTag(tag)"
              >
                #{{ tag }}
              </button>
            </div>
          </div>
        </section>

        <!-- Loading เฉพาะ Trip List (หลังจากเคยโหลดสำเร็จแล้ว) -->
        <TripListSkeleton
          v-if="status === 'loading' && hasLoadedOnce"
        />

        <!-- Error -->
        <ErrorState
          v-else-if="status === 'error'"
          :message="errorMessage"
        />

        <!-- Empty -->
        <EmptyState
          v-else-if="status === 'success' && filteredTrips.length === 0"
          message="ยังไม่มีทริปที่ตรงกับเงื่อนไขการค้นหา ลองเปลี่ยนคำค้นหาหรือตัวกรองดูนะ"
        />

        <!-- Trip List -->
        <section
          v-else-if="status === 'success'"
          class="space-y-4"
        >
          <!-- Summary bar -->
          <div
            class="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-2 mb-1"
          >
            <h2 class="text-lg sm:text-xl font-semibold text-slate-900">
              ผลการค้นหา
            </h2>
            <p class="text-[11px] sm:text-xs text-slate-500">
              พบทั้งหมด
              <span class="font-medium text-slate-700">
                {{ filteredTrips.length }}
              </span>
              ทริปที่ตรงกับเงื่อนไขปัจจุบัน
            </p>
          </div>

          <div class="flex flex-col gap-6">
            <TripCard
              v-for="item in filteredTrips"
              :key="item.id"
              :item="item"
              :keyword="keyword"
              @addKeyword="handleAddKeyword"
            />
          </div>

          <!-- Load more -->
          <div class="flex justify-center mt-4 sm:mt-6">
            <button
              v-if="!lastPage"
              class="px-5 py-2.5 rounded-full border border-sky-500 text-sky-600 text-sm font-medium bg-white hover:bg-sky-50"
              @click="fetchTrips(false)"
            >
              Load more
            </button>
            <p
              v-else
              class="text-[11px] sm:text-xs text-slate-400"
            >
              แสดงทริปทั้งหมดแล้ว
            </p>
          </div>
        </section>
      </template>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

import SearchBar from "@/components/SearchBar.vue";
import TripCard from "@/components/TripCard.vue";
import ErrorState from "@/components/state/ErrorState.vue";
import EmptyState from "@/components/state/EmptyState.vue";
import TripListSkeleton from "@/components/state/TripListSkeleton.vue";
import HomeSkeleton from "@/components/state/HomeSkeleton.vue";

import { getTrips, getTripsMeta } from "@/services/tripService";
import type { Trip } from "@/types/trip";
import { useDebouncedEffect } from "@/composables/useDebouncedEffect";

type Status = "idle" | "loading" | "success" | "error";

const route = useRoute();
const router = useRouter();

const status = ref<Status>("idle");
const hasLoadedOnce = ref(false); // เคยโหลดสำเร็จแล้วอย่างน้อย 1 ครั้งหรือยัง

const trips = ref<Trip[]>([]);
const provinces = ref<string[]>([]);
const tags = ref<string[]>([]);

const selectedProvince = ref("");
const selectedTag = ref("");
const keyword = ref("");

const page = ref(0);
const size = ref(6);
const lastPage = ref(false);

const errorMessage = ref("");

const showAllTags = ref(false);
const shouldShowToggle = computed(() => tags.value.length > 8);

// กันข้อมูลซ้ำ
const loadedIds = new Set<number>();

/* ยูทิลเล็ก ๆ สำหรับ scroll ขึ้นบนสุด */
function scrollToTopSmooth() {
  if (typeof window === "undefined") return;
  window.scrollTo({ top: 0, behavior: "smooth" });
}

/* ดึงค่า state เริ่มต้นจาก route.query (รองรับ back-to-search) */
function applyRouteQueryToState() {
  const q = route.query;

  keyword.value = typeof q.keyword === "string" ? q.keyword : "";
  selectedProvince.value = typeof q.province === "string" ? q.province : "";
  selectedTag.value = typeof q.tag === "string" ? q.tag : "";
}

/* อัปเดต query ให้ตรงกับ state ปัจจุบัน (แชร์ลิงก์ / back ได้) */
function updateRouteQueryFromState() {
  const next: Record<string, string> = {};

  if (keyword.value.trim()) next.keyword = keyword.value.trim();
  if (selectedProvince.value) next.province = selectedProvince.value;
  if (selectedTag.value) next.tag = selectedTag.value;

  router.replace({
    name: "home",
    query: next,
  });
}

/* Load meta */
async function loadMeta() {
  try {
    const meta = await getTripsMeta();
    provinces.value = meta.provinces;
    tags.value = meta.tags;
  } catch (e) {
    console.error(e);
  }
}

/* Filter trip */
const filteredTrips = computed(() => {
  return trips.value.filter((t) => {
    if (selectedProvince.value && t.province !== selectedProvince.value)
      return false;
    if (selectedTag.value && !(t.tags || []).includes(selectedTag.value))
      return false;
    return true;
  });
});

/* Fetch trips */
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

    const unique = result.content.filter((t: Trip) => {
      if (loadedIds.has(t.id)) return false;
      loadedIds.add(t.id);
      return true;
    });

    trips.value.push(...unique);
    lastPage.value = result.last;
    page.value = result.number + 1;

    status.value = "success";
    hasLoadedOnce.value = true; // เคยโหลดสำเร็จแล้ว
  } catch (err: any) {
    console.error(err);
    status.value = "error";
    errorMessage.value = err.message || "โหลดข้อมูลล้มเหลว";
  }
}

/* โหลดให้ครบทุกหน้าเมื่อมีการ filter */
async function ensureAllTripsLoaded() {
  while (!lastPage.value) {
    await fetchTrips(false);
  }
}

watch([selectedProvince, selectedTag], async ([p, t]) => {
  if (p || t) {
    await ensureAllTripsLoaded();
  }
});

/* Debounced Search */
useDebouncedEffect(
  () => {
    fetchTrips(true);
  },
  [keyword],
  300
);

/* Tag clicked from TripCard → เอา tag ไปใส่ในช่องค้นหา + scroll ขึ้นบนสุด */
function handleAddKeyword(tag: string) {
  keyword.value = tag;
  scrollToTopSmooth();
}

/* handler สำหรับปุ่มแท็กด้านบน */
function handleSelectTag(tag: string) {
  selectedTag.value = tag;
  scrollToTopSmooth();
}

function handleSelectAllTag() {
  selectedTag.value = "";
  scrollToTopSmooth();
}

/* sync state <-> route.query */
applyRouteQueryToState();
watch(
  () => route.query,
  () => {
    applyRouteQueryToState();
  }
);

watch([keyword, selectedProvince, selectedTag], () => {
  updateRouteQueryFromState();
});

/* On mount */
onMounted(async () => {
  if ("scrollRestoration" in history) {
    history.scrollRestoration = "manual";
  }

  // เข้าหน้าแรกให้อยู่บนสุด
  setTimeout(() => {
    if (typeof window !== "undefined") {
      window.scrollTo({ top: 0, behavior: "auto" });
    }
  }, 0);

  await loadMeta();
  await fetchTrips(true);
});
</script>

<style scoped></style>