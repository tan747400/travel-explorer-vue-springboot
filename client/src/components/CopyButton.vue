<template>
  <button
    @click="handleCopy"
    title="Copy link"
    aria-label="Copy link"
    class="copy-btn
           order-3 self-end mt-3
           md:absolute md:bottom-4 md:right-4 md:mt-0"
  >
    <span class="copy-btn-inner">
      <LinkIcon
        class="w-4 h-4 md:w-4 md:h-4 text-sky-900"
        :stroke-width="2"
      />
    </span>
  </button>
</template>

<script setup lang="ts">
import { LinkIcon } from "@heroicons/vue/24/outline";
import { useToast } from "vue-toastification";

const toast = useToast();

const props = defineProps<{
  url: string;   // ตอนนี้ส่งมาเป็น "/trips/8"
}>();

async function handleCopy() {
  try {
    // ประกอบ URL ให้เป็น absolute URL
    const fullUrl = new URL(props.url, window.location.origin).href;

    await navigator.clipboard.writeText(fullUrl);

    toast.success("คัดลอกลิงก์แล้ว! 📋");
  } catch {
    toast.error("ไม่สามารถคัดลอกลิงก์ได้");
  }
}
</script>

<style scoped>
.copy-btn {
  @apply w-11 h-11
         md:w-10 md:h-10
         rounded-full
         flex items-center justify-center
         transition-all duration-200 ease-out
         cursor-pointer
         border-none
         bg-transparent
         relative;
}

.copy-btn-inner {
  @apply w-full h-full rounded-full flex items-center justify-center
         shadow-md border border-white/50 transition-all duration-200 ease-out
         bg-gradient-to-br from-sky-100 via-white to-sky-200;
  position: relative;
  overflow: hidden;
}

.copy-btn::before {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: 9999px;
  background: radial-gradient(circle at 30% 0%, rgba(125, 211, 252, 0.45), transparent 60%),
              radial-gradient(circle at 70% 100%, rgba(59, 130, 246, 0.35), transparent 55%);
  opacity: 0;
  filter: blur(6px);
  transition: opacity 0.25s ease-out;
  pointer-events: none;
}

.copy-btn-inner::after {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, transparent 0%, rgba(255,255,255,0.65) 45%, transparent 90%);
  transform: translateX(-120%);
  opacity: 0;
}

.copy-btn:hover .copy-btn-inner {
  transform: translateY(-3px) scale(1.06);
  @apply shadow-lg;
}

.copy-btn:hover::before {
  opacity: 1;
}

.copy-btn:hover .copy-btn-inner::after {
  opacity: 1;
  animation: copy-shine 0.85s ease-out forwards;
}

.copy-btn:active .copy-btn-inner {
  transform: translateY(0) scale(0.92);
  @apply shadow-sm;
}

@keyframes copy-shine {
  0% { transform: translateX(-120%); }
  100% { transform: translateX(120%); }
}
</style>