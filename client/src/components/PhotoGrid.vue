<template>
    <div v-if="photos && photos.length" class="flex gap-2 mt-2">
      <img
        v-for="(photo, i) in photos"
        :key="i"
        class="w-24 h-24 object-cover rounded-lg"
        :src="photo"
        :alt="`${title} ${i + 1}`"
      />
    </div>
  </template>
  
  <script setup lang="ts">
  const props = defineProps<{
    photos: string[];
    title: string;
  }>();
  </script>  