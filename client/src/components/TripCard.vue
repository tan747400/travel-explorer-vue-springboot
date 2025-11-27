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
    <div class="flex flex-col flex-1 gap-1.5">
      <!-- ชื่อทริป -->
      <h2 class="mb-0.5">
        <RouterLink
          :to="detailLink"
          class="text-base sm:text-lg md:text-xl font-semibold text-slate-900
                 hover:text-sky-600 hover:underline line-clamp-2"
        >
          {{ item.title }}
        </RouterLink>
      </h2>

      <!-- ผู้สร้าง / จังหวัด (fallback) -->
      <p class="text-xs text-slate-500 mb-1">
        <span v-if="item.authorName">
          สร้างโดย
          <span class="font-medium text-slate-700">
            {{ item.authorName }}
          </span>
        </span>
        <span v-else-if="item.province">
          สถานที่:
          <span class="font-medium text-slate-700">{{ item.province }}</span>
        </span>
      </p>

      <!-- คำบรรยายสั้น -->
      <p class="text-sm text-slate-700 mb-1 line-clamp-3">
        {{ shortDesc }}
      </p>

      <!-- ลิงก์ไปหน้า detail : เส้นใต้ถึงแค่ตัวหนังสือ + ➜ -->
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

      <!-- แท็ก -->
      <TagList
        class="mt-1"
        :tags="item.tags || []"
        :currentKeyword="keyword"
        @clickTag="handleClickTag"
      />

      <!-- รูปย่อย -->
      <PhotoGrid
        class="mt-2"
        :photos="(item.photos || []).slice(1, 4)"
        :title="item.title"
      />
    </div>

    <!-- ปุ่ม Copy -->
    <CopyButton :url="detailUrl" />
  </article>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { RouterLink, useRoute } from "vue-router";
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

// access สั้น ๆ ใน template
const item = props.item;
const keyword = props.keyword;

// description สั้นไม่เกิน 120 ตัว
const shortDesc = computed(() => {
  const text = (item.description ?? "").trim();
  return text.length > 120 ? text.slice(0, 120) + "…" : text;
});

// query ปัจจุบันของหน้า Home (ใช้ส่งติดไปหน้า detail เพื่อ back-to-search)
const searchQuery = computed(() => {
  const { keyword, province, tag } = route.query;
  const q: Record<string, any> = {};

  if (typeof keyword === "string" && keyword.trim()) {
    q.keyword = keyword;
  }
  if (typeof province === "string" && province) {
    q.province = province;
  }
  if (typeof tag === "string" && tag) {
    q.tag = tag;
  }

  return q;
});

// URL สำหรับ copy (ใช้ path ตรง ๆ)
const detailUrl = computed(() =>
  item.url ? item.url : `/trips/${item.id}`
);

// object ใช้กับ RouterLink (แนบ query ไปด้วย)
const detailLink = computed(() => ({
  name: "trip-detail",
  params: { id: item.id },
  query: searchQuery.value,
}));

function handleClickTag(tag: string) {
  emit("addKeyword", tag);
}
</script>