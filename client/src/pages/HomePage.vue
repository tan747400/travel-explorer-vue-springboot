<template>
  <div class="min-h-screen bg-slate-50">
    <main class="px-4 sm:px-6 lg:px-10 py-8 max-w-6xl mx-auto">
      <!-- Heading -->
      <header class="mb-8">
        <h1
          class="text-4xl sm:text-5xl lg:text-6xl text-center text-sky-500 font-extrabold tracking-tight"
        >
          เที่ยวไหนดี
        </h1>
        <p class="mt-3 text-center text-sm text-slate-500 max-w-xl mx-auto">
          รวมไอเดียสถานที่เที่ยวและทริปในฝันจากผู้ใช้คนอื่น ๆ
          ลองค้นหาที่ที่คุณสนใจดูสิ
        </p>
      </header>

      <!-- Search -->
      <section class="mb-6 w-full">
        <SearchBar
          v-model="keyword"
          label="ค้นหาที่เที่ยว"
          placeholder="หาที่เที่ยวแล้วไปกัน ..."
        />
      </section>

      <!-- ===== Loading: full-page skeleton สำหรับการโหลดครั้งแรก ===== -->
      <HomeSkeleton
        v-if="status === 'loading' && !hasLoadedOnce"
      />

      <!-- ===== Main content (โหลดสำเร็จอย่างน้อย 1 ครั้งแล้ว) ===== -->
      <template v-else>
        <!-- Filters -->
        <!-- ใช้ hasLoadedOnce เพื่อไม่ให้กรอบ Filter หายตอนกำลังค้นหา -->
        <section
          v-if="status === 'success' || hasLoadedOnce"
          class="mb-8 bg-white rounded-2xl border border-slate-200 shadow-sm
                 px-4 sm:px-6 py-4 flex flex-col gap-5"
        >
          <!-- Province -->
          <div class="flex flex-wrap items-center gap-3 text-xs sm:text-sm">
            <span class="text-gray-500 whitespace-nowrap">
              กรองตามสถานที่:
            </span>

            <div class="relative">
              <select
                v-model="selectedProvince"
                class="border border-slate-300 rounded-lg px-3 py-1.5 pr-10
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
                class="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-base font-semibold text-black"
              >
                ▾
              </span>
            </div>
          </div>

          <!-- Tags -->
          <div class="flex flex-col gap-2 w-full">
            <div class="flex justify-between items-center">
              <span class="text-xs sm:text-sm text-gray-500">แท็ก:</span>

              <button
                v-if="shouldShowToggle"
                @click="showAllTags = !showAllTags"
                class="text-[12px] sm:text-xs text-sky-600 hover:text-sky-700 hover:underline"
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
          class="flex flex-col gap-6"
        >
          <TripCard
            v-for="item in filteredTrips"
            :key="item.id"
            :item="item"
            :keyword="keyword"
            @addKeyword="handleAddKeyword"
          />

          <!-- Load more -->
          <div class="flex justify-center mt-6">
            <button
              v-if="!lastPage"
              class="px-5 py-2.5 rounded-full border border-sky-500 text-sky-600 text-sm font-medium bg-white hover:bg-sky-50"
              @click="fetchTrips(false)"
            >
              Load more
            </button>
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
  // เวลาคลิกแท็กจากการ์ด ให้เลื่อนขึ้นไปเห็น header / search ทุกครั้ง
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