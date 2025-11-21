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
        class="mb-8 bg-white rounded-2xl border border-slate-200 shadow-sm px-4 py-4 flex flex-col gap-4"
      >
        <!-- Province Filter -->
        <div class="flex items-center gap-3 text-xs sm:text-sm">
          <span class="text-gray-500 whitespace-nowrap">กรองตามสถานที่:</span>

          <div class="relative">
            <select
              v-model="selectedProvince"
              class="
                border border-slate-300 rounded-lg px-3 py-1.5 pr-10
                bg-white text-xs sm:text-sm shadow-sm
                focus:outline-none focus:ring-2 focus:ring-sky-400
                appearance-none
              "
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

            <!-- Custom caret: ใหญ่ & สีดำ -->
            <span
              class="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-base sm:text-lg font-semibold text-black"
            >
              ▾
            </span>
          </div>
        </div>

        <!-- Tag Filter -->
        <div class="flex flex-col gap-2 w-full">
          <div class="flex items-center justify-between">
            <span class="text-xs sm:text-sm text-gray-500">แท็ก:</span>

            <button
              v-if="tags.length > TAG_LIMIT"
              type="button"
              class="text-[11px] sm:text-xs text-sky-600 hover:text-sky-700 hover:underline whitespace-nowrap"
              @click="showAllTags = !showAllTags"
            >
              {{ showAllTags ? "ซ่อนแท็ก" : "ดูแท็กทั้งหมด" }}
            </button>
          </div>

          <!-- Tags Container -->
          <div class="relative w-full">
            <!-- Mode: Horizontal Scroll (โหมดเริ่มต้น) -->
            <div
              v-if="!showAllTags"
              class="overflow-x-auto no-scrollbar -mx-2 px-2"
            >
              <div class="flex gap-2 py-1 pr-6 w-max">
                <!-- ทั้งหมด -->
                <button
                  type="button"
                  class="px-3 py-1.5 rounded-full border text-[11px] sm:text-xs whitespace-nowrap shadow-sm transition"
                  :class="
                    selectedTag === ''
                      ? 'bg-sky-600 text-white border-sky-600'
                      : 'bg-white text-gray-700 border-gray-300 hover:bg-sky-50'
                  "
                  @click="selectedTag = ''"
                >
                  ทั้งหมด
                </button>

                <!-- Tags -->
                <button
                  v-for="tag in displayedTags"
                  :key="tag"
                  type="button"
                  class="px-3 py-1.5 rounded-full border text-[11px] sm:text-xs whitespace-nowrap shadow-sm transition"
                  :class="
                    selectedTag === tag
                      ? 'bg-sky-600 text-white border-sky-600'
                      : 'bg-white text-gray-700 border-gray-300 hover:bg-sky-50'
                  "
                  @click="selectedTag = tag"
                >
                  #{{ tag }}
                </button>
              </div>
            </div>

            <!-- Mode: Wrapped (ตอนกดดูแท็กทั้งหมด) -->
            <div
              v-else
              class="flex flex-wrap gap-2 py-1 pt-2"
            >
              <button
                type="button"
                class="px-3 py-1.5 rounded-full border text-[11px] sm:text-xs whitespace-nowrap shadow-sm transition"
                :class="
                  selectedTag === ''
                    ? 'bg-sky-600 text-white border-sky-600'
                    : 'bg-white text-gray-700 border-gray-300 hover:bg-sky-50'
                "
                @click="selectedTag = ''"
              >
                ทั้งหมด
              </button>

              <button
                v-for="tag in displayedTags"
                :key="tag"
                type="button"
                class="px-3 py-1.5 rounded-full border text-[11px] sm:text-xs whitespace-nowrap shadow-sm transition"
                :class="
                  selectedTag === tag
                    ? 'bg-sky-600 text-white border-sky-600'
                    : 'bg-white text-gray-700 border-gray-300 hover:bg-sky-50'
                "
                @click="selectedTag = tag"
              >
                #{{ tag }}
              </button>
            </div>

            <!-- Gradient ด้านขวา (desktop เท่านั้น – mobile ไม่บังแท็ก) -->
            <div
              v-if="!showAllTags"
              class="pointer-events-none hidden md:block absolute right-0 top-0 h-full w-12 bg-gradient-to-l from-white to-transparent"
            ></div>
          </div>
        </div>
      </section>

      <!-- States -->
      <Loading v-if="status === 'loading'" />
      <ErrorState v-else-if="status === 'error'" />
      <EmptyState
        v-else-if="status === 'success' && filteredTrips.length === 0"
      />

      <!-- Trip List -->
      <div
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

        <!-- Load More -->
        <div class="flex justify-center mt-6">
          <button
            v-if="!lastPage"
            type="button"
            class="px-5 py-2.5 rounded-full border border-sky-500 text-sky-600 text-sm font-medium
                   bg-white shadow-sm hover:bg-sky-50 disabled:opacity-60 disabled:cursor-not-allowed transition"
            :disabled="loadingMore"
            @click="fetchTrips(false)"
          >
            <span v-if="loadingMore">Loading...</span>
            <span v-else>Load more</span>
          </button>

          <p v-else class="text-sm text-gray-500">
            No more trips to load.
          </p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import SearchBar from "../components/SearchBar.vue";
import TripCard from "../components/TripCard.vue";
import Loading from "../components/state/Loading.vue";
import ErrorState from "../components/state/ErrorState.vue";
import EmptyState from "../components/state/EmptyState.vue";
import { getTrips } from "../services/tripService";
import type { Trip } from "../types/trip";
import { useDebouncedEffect } from "../composables/useDebouncedEffect";

// ⭐️ Toast
import { useToast } from "vue-toastification";
const toast = useToast();

type Status = "idle" | "loading" | "success" | "error";

const trips = ref<Trip[]>([]);
const status = ref<Status>("idle");
const keyword = ref("");

// pagination
const page = ref(0);
const size = ref(6);
const lastPage = ref(false);
const loadingMore = ref(false);

// filters
const selectedProvince = ref<string>("");
const selectedTag = ref<string>("");

// options
const provinces = ref<string[]>([]);
const tags = ref<string[]>([]);

// UI state
const TAG_LIMIT = 12;
const showAllTags = ref(false);

// tags ที่แสดงบน UI
const displayedTags = computed(() => {
  return showAllTags.value ? tags.value : tags.value.slice(0, TAG_LIMIT);
});

// build filter options
function buildFilterOptions(allTrips: Trip[]) {
  const provSet = new Set<string>();
  const tagSet = new Set<string>();

  for (const t of allTrips) {
    if (t.province) {
      provSet.add(t.province);
    }
    if (t.tags) {
      t.tags.forEach((tag) => {
        if (tag && tag.trim().length > 0) {
          tagSet.add(tag.trim());
        }
      });
    }
  }

  provinces.value = Array.from(provSet).sort((a, b) =>
    a.localeCompare(b, "th")
  );
  tags.value = Array.from(tagSet).sort((a, b) =>
    a.localeCompare(b, "th")
  );

  if (tags.value.length <= TAG_LIMIT) {
    showAllTags.value = false;
  }
}

// filter list
const filteredTrips = computed(() => {
  return trips.value.filter((t) => {
    if (selectedProvince.value && t.province !== selectedProvince.value) {
      return false;
    }

    if (selectedTag.value) {
      const list = t.tags || [];
      if (!list.includes(selectedTag.value)) {
        return false;
      }
    }
    return true;
  });
});

// fetch trips
async function fetchTrips(reset: boolean) {
  try {
    if (reset) {
      status.value = "loading";
      trips.value = [];
      page.value = 0;
      lastPage.value = false;
    } else {
      loadingMore.value = true;
    }

    const result = await getTrips(keyword.value, page.value, size.value);

    trips.value = reset ? result.content : [...trips.value, ...result.content];

    lastPage.value = result.last;
    page.value = result.number + 1;

    buildFilterOptions(trips.value);
    status.value = "success";
  } catch (err: any) {
    console.error(err);
    const message =
      err?.message || "โหลดข้อมูลล้มเหลว กรุณาลองใหม่อีกครั้ง";

    if (reset) {
      status.value = "error";
    }

    // แจ้งผู้ใช้ด้วย toast
    toast.error(message);
  } finally {
    loadingMore.value = false;
  }
}

// debounce keyword → ยิง request ใหม่
useDebouncedEffect(
  () => {
    selectedProvince.value = "";
    selectedTag.value = "";
    showAllTags.value = false;
    fetchTrips(true);
  },
  [keyword],
  300
);

// first load
onMounted(() => {
  fetchTrips(true);
});

function handleAddKeyword(tag: string) {
  const tokens = keyword.value
    .trim()
    .split(" ")
    .filter((t) => t.length > 0);

  if (!tokens.includes(tag)) {
    keyword.value = keyword.value ? `${keyword.value} ${tag}` : tag;
  }
}
</script>

<style scoped>
</style>