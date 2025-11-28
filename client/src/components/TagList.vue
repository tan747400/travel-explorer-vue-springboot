<template>
  <div class="flex flex-wrap gap-2 mb-2">
    <div
      v-for="(tag, idx) in tags"
      :key="idx"
      class="flex items-center gap-1"
    >
      <!-- ถ้าเป็นตัวสุดท้ายและไม่ใช่แท็กแรก → แสดง "และ" -->
      <span
        v-if="idx === tags.length - 1 && idx !== 0"
        class="text-sm text-gray-500"
      >
        และ
      </span>

      <!-- ปุ่ม tag คลิกได้ -->
      <button
        type="button"
        class="text-sm text-gray-600 hover:text-blue-500"
        :aria-pressed="currentKeyword.includes(tag)"
        @click="$emit('clickTag', tag)"
      >
        {{ tag }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  tags: string[];
  currentKeyword: string;
}>();

defineEmits<{
  (e: "clickTag", tag: string): void;
}>();
</script>