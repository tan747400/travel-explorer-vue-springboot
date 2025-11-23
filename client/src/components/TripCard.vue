<template>
  <div
    class="relative flex flex-col md:flex-row items-start gap-4 p-4 shadow-sm hover:shadow-md transition bg-white rounded-3xl"
  >
    <!-- รูปหลัก -->
    <img
      v-if="props.item.photos?.[0]"
      class="rounded-3xl w-full md:w-[350px] h-[250px] object-cover order-1"
      :src="props.item.photos[0]"
      :alt="props.item.title"
    />

    <!-- เนื้อหา -->
    <div class="flex flex-col flex-1 order-2">
      <!-- ชื่อทริป -->
      <h2 class="mb-1">
        <RouterLink
          :to="{ name: 'trip-detail', params: { id: props.item.id } }"
          class="text-xl font-bold hover:underline"
        >
          {{ props.item.title }}
        </RouterLink>
      </h2>

      <!-- จังหวัด -->
      <p
        v-if="props.item.province"
        class="text-sm text-sky-600 font-medium mb-1"
      >
        {{ props.item.province }}
      </p>

      <!-- คำบรรยายสั้น -->
      <p class="text-gray-700 mb-2">
        {{ shortDesc }}
      </p>

      <!-- ลิงก์ไปหน้า Detail -->
      <RouterLink
        :to="{ name: 'trip-detail', params: { id: props.item.id } }"
        class="text-blue-500 underline mb-2 text-sm"
      >
        View Detail
      </RouterLink>

      <!-- แท็ก -->
      <TagList
        :tags="props.item.tags || []"
        :currentKeyword="props.keyword"
        @clickTag="handleClickTag"
      />

      <!-- รูปย่อย -->
      <PhotoGrid
        :photos="(props.item.photos || []).slice(1, 4)"
        :title="props.item.title"
      />
    </div>

    <!-- ปุ่ม Copy -->
    <CopyButton :url="detailUrl" />
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { RouterLink } from "vue-router";
import TagList from "./TagList.vue";
import PhotoGrid from "./PhotoGrid.vue";
import CopyButton from "./CopyButton.vue";
import type { Trip } from "@/types/trip";

// ให้ TypeScript เห็นว่าเราใช้ props จริง ๆ
const props = defineProps<{
  item: Trip & { url?: string };
  keyword: string;
}>();

const emit = defineEmits<{
  (e: "addKeyword", tag: string): void;
}>();

// description สั้นไม่เกิน 120 ตัว
const shortDesc = computed(() => {
  const text = (props.item.description ?? "").trim();
  return text.length > 120 ? text.slice(0, 120) + "…" : text;
});

const detailUrl = computed(() =>
  props.item.url ? props.item.url : `/trips/${props.item.id}`
);

function handleClickTag(tag: string) {
  emit("addKeyword", tag);
}
</script>