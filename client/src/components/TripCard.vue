<template>
  <div
    class="relative flex flex-col md:flex-row items-start gap-4 p-4 shadow-sm hover:shadow-md transition"
  >
    <!-- รูปหลัก -->
    <img
      v-if="item.photos?.[0]"
      class="rounded-3xl w-full md:w-[350px] h-[250px] object-cover order-1"
      :src="item.photos[0]"
      :alt="item.title"
    />

    <!-- เนื้อหา -->
    <div class="flex flex-col flex-1 order-2">
      <h2 class="mb-2">
        <a
          :href="detailUrl"
          target="_blank"
          rel="noopener noreferrer"
          class="text-xl font-bold hover:underline"
        >
          {{ item.title }}
        </a>
      </h2>

      <p class="text-gray-700 mb-2">
        {{
          item.description.length > 100
            ? item.description.slice(0, 100) + "..."
            : item.description
        }}
      </p>

      <a
        :href="detailUrl"
        target="_blank"
        rel="noopener noreferrer"
        class="text-blue-500 underline mb-2"
      >
        อ่านต่อ
      </a>

      <TagList
        :tags="item.tags || []"
        :currentKeyword="keyword"
        @clickTag="handleClickTag"
      />

      <PhotoGrid
        :photos="(item.photos || []).slice(1, 4)"
        :title="item.title"
      />
    </div>

    <CopyButton :url="detailUrl" />
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import TagList from "./TagList.vue";
import PhotoGrid from "./PhotoGrid.vue";
import CopyButton from "./CopyButton.vue";
import type { Trip } from "../types/trip";

const props = defineProps<{
  item: Trip & { url?: string }; // เผื่อมี url จาก backend
  keyword: string;
}>();

const emit = defineEmits<{
  (e: "addKeyword", tag: string): void;
}>();

// ถ้ามี item.url ก็ใช้เลย ไม่งั้น fallback ไป internal route
const detailUrl = computed(() =>
  props.item.url ? props.item.url : `/trips/${props.item.id}`
);

function handleClickTag(tag: string) {
  emit("addKeyword", tag);
}
</script>