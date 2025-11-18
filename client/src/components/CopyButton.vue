<template>
    <button
      @click="handleCopy"
      title="Copy link"
      aria-label="Copy link"
      class="order-3 self-end mt-3
             w-12 h-12 md:w-14 md:h-14
             rounded-full bg-white
             border-2 border-sky-400
             ring-2 ring-sky-200
             flex items-center justify-center
             hover:bg-sky-50 active:scale-95
             transition
             md:absolute md:bottom-3 md:right-3 md:mt-0"
    >
      <LinkIcon
        class="w-6 h-6 md:w-7 md:h-7 text-sky-500 origin-center rotate-45"
        :stroke-width="2"
      />
    </button>
  </template>
  
  <script setup lang="ts">
  import { LinkIcon } from "@heroicons/vue/24/outline";
  
  const props = defineProps<{
    url: string;
  }>();
  
  async function handleCopy() {
    try {
      await navigator.clipboard.writeText(props.url);
      alert("Copied to clipboard!");
    } catch {
      alert("ไม่สามารถคัดลอกลิงก์ได้");
    }
  }
  </script>  