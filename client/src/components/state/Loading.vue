<template>
  <div class="w-full flex flex-col items-center justify-center py-12 text-center">
    <div class="loader"></div>

    <p class="mt-3 text-sm text-gray-500 animate-pulse">
      กำลังโหลดข้อมูล...
    </p>
  </div>
</template>

<style scoped>
.loader {
  width: 36px;
  height: 36px;
  border: 4px solid rgba(14, 165, 233, 0.2);
  border-top-color: rgb(14, 165, 233);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  100% {
    transform: rotate(360deg);
  }
}
</style>