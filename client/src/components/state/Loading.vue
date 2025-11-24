<template>
  <div class="w-full flex flex-col items-center justify-center py-12 text-center">
    <div class="loader"></div>

    <p class="mt-3 text-sm text-slate-500 animate-pulse">
      กำลังโหลดข้อมูล...
    </p>
  </div>
</template>

<style scoped>
.loader {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(14, 165, 233, 0.25);
  border-top-color: rgb(14, 165, 233);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  opacity: 0.9;
}

@keyframes spin {
  100% {
    transform: rotate(360deg);
  }
}
</style>